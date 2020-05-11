package battlecity.flash

import battlecity.stage.Grid
import battlecity.view.City

// 坦克生成类
internal class Spawn(var x: Int, val y: Int, var existTime: Int = 56) {
    // 将坦克生成位置的地图元素设为空格
    internal fun checkLocation() {
        val row = y - 16 shr 4
        val column = x - 32 shr 4

        if (City.stage.map[row][column].type != Grid.Type.BLANK) {
            City.stage.map[row - 1][column - 1].type = Grid.Type.BLANK
            City.stage.map[row - 1][column].type = Grid.Type.BLANK
            City.stage.map[row][column - 1].type = Grid.Type.BLANK
            City.stage.map[row][column].type = Grid.Type.BLANK
        }
    }
}