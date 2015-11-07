package no.nextgentel.oss.akkatools.aggregate.testAggregate

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Status.Failure
import akka.actor.{ActorPath, ActorSystem, Props}
import no.nextgentel.oss.akkatools.aggregate._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.FiniteDuration


case class PrintTicketMessage(id:String)
case class CinemaNotification(seatsBooked:List[String])

// Aggregate
class BookingAggregate(ourDispatcherActor: ActorPath, ticketPrintShop: ActorPath, cinemaNotifier: ActorPath, var predefinedSeatIds:List[String] = List())
  extends GeneralAggregate[BookingEvent, BookingState](ourDispatcherActor) {


  // Used as prefix/base when constructing the persistenceId to use - the unique ID is extracted runtime from actorPath which is construced by Sharding-coordinator
  override def persistenceIdBase() = BookingAggregate.persistenceIdBase

  // Override this one to set different timeout
  override def idleTimeout() = FiniteDuration(60, TimeUnit.SECONDS)

  var state = BookingState.empty() // This is our initial state(Machine)

  def generateNextSeatId():String = {

    if (predefinedSeatIds.isEmpty) {
      UUID.randomUUID().toString
    } else {
      // pop the first id
      val id = predefinedSeatIds(0)
      predefinedSeatIds = predefinedSeatIds.tail
      id
    }
  }

  // transform command to event
  override def cmdToEvent = {
    case c: OpenBookingCmd  =>  ResultingEvent(BookingOpenEvent(c.seats))

    case c: CloseBookingCmd => ResultingEvent(BookingClosedEvent())

    case c: ReserveSeatCmd  =>
      // Generate a random seatId
      val seatId = generateNextSeatId()
      val event = ReservationEvent(seatId)

      ResultingEvent(event)
        .onSuccess{
          sender ! seatId } // Send the seatId back
        .onError ( errorMsg => sender ! Failure(new Exception(errorMsg)) )
        .onAfterValidationSuccess{
          if (c.shouldFailIn_onAfterValidationSuccess) {
            throw BookingError("Failed in onAfterValidationSuccess")
          }
        }

    case c: CancelSeatCmd =>
      ResultingEvent(CancelationEvent(c.seatId))
        .onSuccess( sender ! "ok")
        .onError( (errorMsg) => sender ! Failure(new Exception(errorMsg)) )
  }

  override def generateResultingDurableMessages = {
    case e: BookingClosedEvent =>
      // The booking has now been closed and we need to send an important notification to the Cinema
      val cinemaNotification = CinemaNotification(state.reservations.toList)
      ResultingDurableMessages(cinemaNotification, cinemaNotifier)

    case e: ReservationEvent =>
      // The seat-reservation has been confirmed and we need to print the ticket

      val printShopMessage = PrintTicketMessage(e.id)
      ResultingDurableMessages(printShopMessage, ticketPrintShop)
  }
}



object BookingAggregate {

  val persistenceIdBase = "booking-"

  def props(ourDispatcherActor: ActorPath, ticketPrintShop: ActorPath, cinemaNotifier: ActorPath, predefinedSeatIds:List[String]) =
    Props(new BookingAggregate(ourDispatcherActor, ticketPrintShop, cinemaNotifier, predefinedSeatIds))
}


// Setting up the builder we're going to use for our BookingAggregate and view
class BookingAggregateBuilder(actorSystem: ActorSystem) extends GeneralAggregateBuilder[BookingEvent, BookingState](actorSystem) {


  override def persistenceIdBase() = BookingAggregate.persistenceIdBase

  def config(ticketPrintShop: ActorPath, cinemaNotifier: ActorPath, predefinedSeatIds:List[String]): Unit = {
    withGeneralAggregateProps {
      ourDispatcher: ActorPath =>
        BookingAggregate.props(ourDispatcher, ticketPrintShop, cinemaNotifier, predefinedSeatIds)
    }
  }

  // Override this method to create Initial states for views
  override def createInitialState(aggregateId: String): BookingState = BookingState.empty()
}