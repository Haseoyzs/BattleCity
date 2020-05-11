package battlecity.util

import battlecity.flash.Power
import battlecity.movable.Tank
import battlecity.stage.Grid
import battlecity.view.City

internal object Dynamic {
    // 敌方坦克回头开炮的概率
    internal fun randomBranch(): Boolean {
        return Math.random() <= 0.25
    }

    // 随机生成敌方坦克类型
    internal fun randomTankType(): Tank.Type {
        return Tank.Type.values()[(Math.random() * 4).toInt()]
    }

    internal fun randomDirection(): Tank.Direction {
        return when ((Math.random() * 8).toInt()) {
            in 0 until 1 -> Tank.Direction.values()[0]
            in 1 until 4 -> Tank.Direction.values()[1]
            in 4 until 6 -> Tank.Direction.values()[2]
            else -> Tank.Direction.values()[3]
        }
    }
    internal fun randomWithoutUp(): Tank.Direction {
        return when((Math.random() * 3).toInt()) {
            in 0 until 1 -> Tank.Direction.values()[1]
            in 1 until 2 -> Tank.Direction.values()[2]
            else -> Tank.Direction.values()[3]
        }
    }
    internal fun randomWithoutDown(): Tank.Direction {
        return when((Math.random() * 3).toInt()) {
            in 0 until 1 -> Tank.Direction.values()[0]
            in 1 until 2 -> Tank.Direction.values()[2]
            else -> Tank.Direction.values()[3]
        }
    }
    internal fun randomWithoutLeft(): Tank.Direction {
        return when((Math.random() * 3).toInt()) {
            in 0 until 1 -> Tank.Direction.values()[0]
            in 1 until 2 -> Tank.Direction.values()[1]
            else -> Tank.Direction.values()[3]
        }
    }
    internal fun randomWithoutRight(): Tank.Direction {
        return when((Math.random() * 3).toInt()) {
            in 0 until 1 -> Tank.Direction.values()[0]
            in 1 until 2 -> Tank.Direction.values()[1]
            else -> Tank.Direction.values()[2]
        }
    }


    private  fun cantTouch(row: Int, column: Int): Boolean {
        return (City.stage.map[row][column].type == Grid.Type.STEEL_WALL || City.stage.map[row][column].type == Grid.Type.RIVER)
                && (City.stage.map[row][column + 1].type == Grid.Type.STEEL_WALL || City.stage.map[row][column + 1].type == Grid.Type.RIVER)
                && (City.stage.map[row + 1][column].type == Grid.Type.STEEL_WALL || City.stage.map[row + 1][column].type == Grid.Type.RIVER)
                && (City.stage.map[row + 1][column + 1].type == Grid.Type.STEEL_WALL || City.stage.map[row + 1][column + 1].type == Grid.Type.RIVER)
    }
    internal fun randomLocation() {
        val row = (Math.random() * 338).toInt() shr 4
        val column = (Math.random() * 384).toInt() shr 4

        City.power.x = (column shl 4) + 32
        City.power.y = (row shl 4) + 16

        if (cantTouch(row, column)) {
            randomLocation()
        }
    }

    // 随机生成道具类型
    internal fun randomPower(): Power.Type {
        return Power.Type.values()[(Math.random() * 6).toInt()]
    }
}