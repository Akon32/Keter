package ru.org.codingteam.keter.game.objects.behaviors

import ru.org.codingteam.keter.game.GameState
import ru.org.codingteam.keter.game.actions.{MoveAction, WaitAction}
import ru.org.codingteam.keter.game.objects.{ActorBehavior, Actor}

import scala.concurrent.Future
import scala.util.Random

object RandomBehavior extends ActorBehavior {

  val random = Random

  override def getNextAction(actor: Actor, state: GameState) = Future.successful({
    val (x, y) = (random.nextInt(3)-1, random.nextInt(3)-1)
    if (x == 0 && y == 0) {
      WaitAction(actor)
    } else {
      MoveAction(actor, x, y)
    }
  })

}