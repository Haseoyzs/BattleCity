package battlecity.movable

import battlecity.stage.Grid
import battlecity.view.City
import java.awt.Rectangle

// 坦克类
internal open class Tank(var x: Int, var y: Int, var direction: Direction) {
    internal enum class Group { PLAYER1, PLAYER2, ENEMY }
    internal enum class Type { TYPE1, TYPE2, TYPE3, TYPE4 }
    internal enum class Direction { UP, DOWN, LEFT, RIGHT }

    internal var speed = 2
    internal var halfW = 13
    internal var halfH = 15
    internal var health = 0
    private var oldDirection = direction

    internal var alive = true
    internal var moving = true
    internal var flashing = false
    internal var touchIce = false

    // 获取坦克矩形
    internal fun getRect(): Rectangle {
        return when (direction) {
            Direction.UP, Direction.DOWN -> Rectangle(x - halfW, y - halfH, halfW shl 1, halfH shl 1)
            Direction.LEFT, Direction.RIGHT -> Rectangle(x - halfH, y - halfW, halfH shl 1, halfW shl 1)
        }
    }

    // 检测是否碰到其他物体
    private fun isTouchObject(selfX: Int, selfY: Int, selfHalfH: Int, anotherDirection: Direction, anotherX: Int, anotherY: Int, anotherHalfW: Int, anotherHalfH: Int): Boolean {
        return when (anotherDirection) {
            Direction.UP, Direction.DOWN -> (selfX in anotherX - anotherHalfW..anotherX + anotherHalfW || selfX - halfW in anotherX - anotherHalfW..anotherX + anotherHalfW
                    || selfX + halfW in anotherX - anotherHalfW..anotherX + anotherHalfW) && (selfY + selfHalfH in anotherY - anotherHalfH..anotherY + anotherHalfH)
            Direction.RIGHT, Direction.LEFT -> (selfX in anotherX - anotherHalfH..anotherX + anotherHalfH || selfX - halfW in anotherX - anotherHalfH..anotherX + anotherHalfH
                    || selfX + halfW in anotherX - anotherHalfH..anotherX + anotherHalfH) && (selfY + selfHalfH in anotherY - anotherHalfW..anotherY + anotherHalfW)
        }
    }
    // 检测是否碰到坦克
    private fun isTouchTank(tanks: List<Tank>): Boolean {
        if (alive) {
            tanks.forEach {
                if (it.alive && it !== this) {
                    when (direction) {
                        Direction.UP -> if (isTouchObject(x, y, -16, it.direction, it.x, it.y, it.halfW, it.halfH)) return false
                        Direction.DOWN -> if (isTouchObject(x, y, 16, it.direction, it.x, it.y, it.halfW, it.halfH)) return false
                        Direction.LEFT -> if (isTouchObject(y, x, -16, it.direction, it.y, it.x, it.halfW, it.halfH)) return false
                        Direction.RIGHT -> if (isTouchObject(y, x, 16, it.direction, it.y, it.x, it.halfW, it.halfH)) return false
                    }
                }
            }
        }
        return true
    }

    // 修正坦克位置
    private fun setLocation() {
        if (direction != oldDirection) {
            when(direction) {
                Direction.UP, Direction.DOWN -> x = x + 8 shr 4 shl 4
                Direction.LEFT, Direction.RIGHT -> y = y + 8 shr 4 shl 4
            }
        }
        oldDirection = direction
    }
    // 检测是否碰到其他地图元素
    private fun isTouchElement(row: Int, column: Int): Boolean {
        val i = when {
            row < 0 -> 0
            row > 25 -> 25
            else -> row
        }
        val j = when {
            column < 0 -> 0
            column > 25 -> 25
            else -> column
        }

        if (City.stage.map[i][j].type == Grid.Type.ICE_FIELD) {
            touchIce = true
            return true
        } else {
            touchIce = false
        }

        return City.stage.map[i][j].type == Grid.Type.BLANK
                || City.stage.map[i][j].type == Grid.Type.WOOD
    }
    // 当坦克向上时
    internal fun onMoveUp(): Boolean {
        setLocation()

        val x = this@Tank.x - 32
        val y = this@Tank.y - 16

        val row = y - 16 - speed shr 4
        val leftColumn = x - halfW shr 4
        val midColumn = x shr 4
        val rightColumn = x + halfW shr 4

        return isTouchElement(row, leftColumn) && isTouchElement(row, midColumn)
                && isTouchElement(row, rightColumn) && isTouchTank(City.players)
                && isTouchTank(City.enemies)
    }
    // 当坦克向下时
    internal fun onMoveDown(): Boolean {
        setLocation()

        val x = this@Tank.x - 32
        val y = this@Tank.y - 16

        val row = y + 16 + speed shr 4
        val leftColumn = x - halfW shr 4
        val midColumn = x shr 4
        val rightColumn = x + halfW shr 4

        return isTouchElement(row, leftColumn) && isTouchElement(row, midColumn)
                && isTouchElement(row, rightColumn) && isTouchTank(City.players)
                && isTouchTank(City.enemies)
    }
    // 当坦克向左时
    internal fun onMoveLeft(): Boolean {
        setLocation()

        val x = this@Tank.x - 32
        val y = this@Tank.y - 16

        val column = x - 16 - speed shr 4
        val topRow = y - halfW shr 4
        val midRow = y shr 4
        val bottomRow = y + halfW shr 4

        return isTouchElement(topRow, column) && isTouchElement(midRow, column)
                && isTouchElement(bottomRow, column) && isTouchTank(City.players)
                && isTouchTank(City.enemies)
    }
    // 当坦克向右时
    internal fun onMoveRight(): Boolean {
        setLocation()

        val x = this@Tank.x - 32
        val y = this@Tank.y - 16

        val column = x + 16 + speed shr 4
        val topRow = y - halfW shr 4
        val midRow = y shr 4
        val bottomRow = y + halfW shr 4

        return isTouchElement(topRow, column) && isTouchElement(midRow, column)
                && isTouchElement(bottomRow, column) && isTouchTank(City.players)
                && isTouchTank(City.enemies)
    }
}