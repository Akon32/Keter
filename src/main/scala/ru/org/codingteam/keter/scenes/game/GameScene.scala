package ru.org.codingteam.keter.scenes.game

import org.scalajs.dom.KeyboardEvent
import ru.org.codingteam.keter.game.Engine
import ru.org.codingteam.keter.game.actions._
import ru.org.codingteam.keter.game.objects.behaviors.PlayerBehavior
import ru.org.codingteam.keter.game.objects.{Actor, ActorActive, ActorInactive}
import ru.org.codingteam.keter.map.{Move, RenderUtils}
import ru.org.codingteam.keter.scenes.Scene
import ru.org.codingteam.keter.util.Logging
import ru.org.codingteam.rotjs.interface.{Display, ROT}
import ru.org.codingteam.rotjs.wrappers._

class GameScene(display: Display, engine: Engine) extends Scene(display) with Logging {

  override protected def onKeyDown(event: KeyboardEvent): Unit = {
    lazy val player = this.player
    if (event.keyCode == ROT.VK_NUMPAD5) {
      processAction(WaitAction(player))
    } else {
      val move = event.keyCode match {
        case x if x == ROT.VK_NUMPAD8 || x == ROT.VK_UP =>
          Some(Move(0, -1))
        case x if x == ROT.VK_NUMPAD9 =>
          Some(Move(1, -1))
        case x if x == ROT.VK_NUMPAD6 || x == ROT.VK_RIGHT =>
          Some(Move(1, 0))
        case x if x == ROT.VK_NUMPAD3 =>
          Some(Move(1, 1))
        case x if x == ROT.VK_NUMPAD2 || x == ROT.VK_DOWN =>
          Some(Move(0, 1))
        case x if x == ROT.VK_NUMPAD1 =>
          Some(Move(-1, 1))
        case x if x == ROT.VK_NUMPAD4 || x == ROT.VK_LEFT =>
          Some(Move(-1, 0))
        case x if x == ROT.VK_NUMPAD7 =>
          Some(Move(-1, -1))
        case _ => None
      }
      move map { m =>
        val target = player.position.moveWithJumps(m).objectPosition
        state.findActors(target) match {
          case Nil => processAction(WalkAction(player, m))
          case as => as foreach (_ => processAction(MeleeAttackAction(player, target)))
        }
      }
    }
    render()
  }

  override protected def render(): Unit = {
    display.clear()

    log.debug("Drawing field")
    val offset = 10
    val fieldView = display.viewport(1, 1, display.width - 2, display.height - 5, offset, offset)
    val tiles = RenderUtils.renderToSeq(engine.universe, -offset, offset, -offset, offset)
    tiles foreach {
      case (x, y, tile) => fieldView.draw(x, y, tile)
    }
    // display stats.
    val statsView = display.viewport(0, display.height - 2, display.width, 2)
    statsView.drawTextCentered(s"Faction/Name: ${player.faction.name}/${player.name}", Some(0))
    statsView.drawTextCentered(s"Health: ${player.stats.health} Time passed: ${state.timestamp}", Some(1))
  }

  private def player = state.player

  private def state = engine.universe.current

  private def processAction(action: Action): Unit = {
    log.debug(s"Scheduling player action: $action")
    player.behavior match {
      case pb: PlayerBehavior =>
        pb.nextAction.success(action)
    }
  }

  private def getColor(actor: Actor) = {
    actor.state match {
      case ActorActive => null
      case ActorInactive => "#aaa"
    }
  }

}
