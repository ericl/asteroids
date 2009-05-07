import java.awt._
import javax.swing._
import java.util.List
import scala.actors._
import scala.actors.Actor
import scala.actors.Actor._
import net.phys2d.raw._
import net.phys2d.raw.Body
import net.phys2d.math._
import net.phys2d.raw.strategies._
import asteroids._
import asteroids.bodies._
import asteroids.display._
import asteroids.handlers._
import asteroids.handlers.Timer

object Main extends Application {
  implicit def makeVector(t: Tuple2[Number, Number]): Vector2f = new Vector2f(t._1.floatValue, t._2.floatValue)
  implicit def makeFloat(n: Number): Float = n.floatValue

  def range(min: Number, max: Number): Number = min + (max - min) * Math.random

  def add(pos: ROVector2f) = world ! Insert({
    val body: Body = new BigAsteroid(range(5, 50)) with PhysicsObj
    val coords = display.getOffscreenCoords(20, 0, pos)
    body.setPosition(coords.getX, coords.getY)
    body.adjustAngularVelocity(range(-Math.Pi, Math.Pi))
    body.adjustVelocity((range(-100, 100), range(-100, 100)));
    body
  })

  val canvas = new Canvas()
  val frame = new JFrame("Asteroids in Scala")
  val dimension = new Dimension(500, 500)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setLocationByPlatform(true)
  frame.setSize(dimension)
  frame.add(canvas)

  val timer = new Timer(60)
  val display = new Display(frame, dimension, canvas)
  val world = new WorldController(display)
  val stars = new StarField(display)
  stars.init()

  var x = 0
  var y = 0
  world.start
  while (true) {
    stars.starField()
    world !? Paint(display)
    display.show()
    val dt = timer.tick()
    add(world.pickPos)
    world !? Step(dt)
    display.setCenter(world.pickPos)
    x += 1
    y += 1
  }
}

trait PhysicsObj extends Body with Explodable with Actor {
  start
  def act() {
    loop {
      react {
        case Impact(other: Body, event: CollisionEvent) =>
          if (other.getMass() >= getMass()) {
            sender ! Fragment(this, other, getFragments, getRemnant)
            exit()
          }
      }
    }
  }
}

case class Step(dt: Float)
case class Paint(display: Display)
case class Impact(other: Body, event: CollisionEvent)
case class Insert(body: Body)
case class Destroy(body: PhysicsObj)
case class Fragment(body: PhysicsObj, other: Body, fragments: List[Body], remnant: Body)
case class Collide(event: CollisionEvent)

import Main._
class WorldController(display: Display) extends CollisionListener with Actor {
  private val world: World = new World((0,0), 10, new QuadSpaceStrategy(20, 5))
  private val exploder: Exploder = new Exploder(world, display)
  world.addListener(this)

  def collisionOccured(event: CollisionEvent) = this ! Collide(event)

  def pickPos(): ROVector2f = {
    val bodies = world.getBodies();
    var i = 0
    while (i < bodies.size) {
      val body = bodies.get(i)
      if (body.isInstanceOf[PhysicsObj])
        return body.getPosition()
      i += 1
    }
    return (0,0)
  }

  def act() {
    loop {
      receive {
        case Step(dt) =>
          world.step(dt)
          val bodies = world.getBodies();
          var i = 0
          while (i < bodies.size) {
            val body = bodies.get(i)
            if (!display.inView(body.getPosition(), 100))
              world.remove(body)
            i += 1
          }
          reply()
        case Paint(display) =>
          display.drawWorld(world)
          reply()
        case Insert(body) =>
          world.add(body)
        case Destroy(body) =>
          world.remove(body)
        case Fragment(body, other, fragments, remnant) =>
          // NOTE: funny behavior is because of async nature:
          // bodies have changed velocities since collision
          exploder.fragment(body, other)
        case Collide(event) =>
          val A = event.getBodyA()
          val B = event.getBodyB()
          if (A.isInstanceOf[PhysicsObj])
            A.asInstanceOf[PhysicsObj] ! Impact(B, event)
          if (B.isInstanceOf[PhysicsObj])
            B.asInstanceOf[PhysicsObj] ! Impact(A, event)
      }
    }
  }
}
