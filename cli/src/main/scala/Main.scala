package lila.cli

import scalaz.effects._
import play.api.{ Mode, Application }

import java.io.File
import lila.core.CoreEnv

object Main {

  lazy val app = new Application(
    path = new File("."),
    classloader = this.getClass.getClassLoader(),
    sources = None,
    mode = Mode.Dev)

  lazy val env = CoreEnv(app)

  lazy val users = Users(env.user.userRepo, env.security.store)
  lazy val games = Games(env)
  lazy val titivate = env.titivate
  lazy val forum = Forum(env.forum)
  lazy val infos = Infos(env)

  def main(args: Array[String]): Unit = sys exit {

    val op: IO[Unit] = args.toList match {
      case "average-elo" :: Nil ⇒ infos.averageElo
      case "trans-js-dump" :: Nil ⇒ TransJsDump(
        path = new File(env.app.path.getCanonicalPath + "/public/trans"),
        pool = env.i18n.pool,
        keys = env.i18n.keys).apply
      case "user-enable" :: username :: Nil  ⇒ users enable username
      case "user-disable" :: username :: Nil ⇒ users disable username
      case "forum-denormalize" :: Nil        ⇒ forum.denormalize
      case "forum-typecheck" :: Nil          ⇒ forum.typecheck
      case "game-cleanup-next" :: Nil        ⇒ titivate.cleanupNext
      case "game-cleanup-unplayed" :: Nil    ⇒ titivate.cleanupUnplayed
      case "game-finish" :: Nil              ⇒ titivate.finishByClock
      case _                                 ⇒ putStrLn("Usage: run command args")
    }
    op.map(_ ⇒ 0).unsafePerformIO
  }
}
