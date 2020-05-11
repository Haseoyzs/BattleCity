package battlecity.stage

import battlecity.util.Config

// 关卡类
internal class Stage {
    // 创建森林集合
    internal val woods = ArrayList<Grid>()
    // 创建地图集合
    internal val map = Array(26) { Array(26) { Grid(0, 0, Grid.Type.BLANK) } }

    // 加载地图
    internal fun loadMap() {
        woods.clear()
        // 按行读取地图文件中的数据
        Stage::class.java.getResourceAsStream("/Stages/STAGE${Config.stageIndex}").bufferedReader().useLines { lines ->
            for ((index, value) in lines.withIndex()) {
                for (i in 0..25) {
                    when (value[i]) {
                        '0' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.BLANK)
                        '1' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.BRICK_WALL)
                        '2' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.STEEL_WALL)
                        '3' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.RIVER)
                        '4' -> {
                            map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.WOOD)
                            woods.add(map[index][i])
                        }
                        '5' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.ICE_FIELD)
                        '6' -> map[index][i] = Grid((i shl 4) + 32, (index shl 4) + 16, Grid.Type.BASE)
                    }
                }
            }
        }
    }

    // 将基地的墙修改为砖墙
    internal fun changeBaseWallToBrick() {
        for (i in 23..25) {
            for (j in 11..14) {
                if (i == 23 || ((i == 24 || i == 25) && (j == 11 || j == 14))) {
                    map[i][j].part = Grid.Part.WHOLE
                    map[i][j].type = Grid.Type.BRICK_WALL
                }
            }
        }
    }
    // 将基地的墙修改了铁墙
    internal fun changeBaseWallToSteel() {
        for (i in 23..25) {
            for (j in 11..14) {
                if (i == 23 || ((i == 24 || i == 25) && (j == 11 || j == 14))) {
                    map[i][j].part = Grid.Part.WHOLE
                    map[i][j].type = Grid.Type.STEEL_WALL
                }
            }
        }
    }
}