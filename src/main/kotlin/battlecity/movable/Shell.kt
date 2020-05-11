package battlecity.movable

import java.awt.Rectangle

// 炮弹类
internal class Shell(val direction: Tank.Direction, var x: Int, var y: Int, val group: Tank.Group, val type: Tank.Type) {
    internal var speed = 4
    internal var alive = true

    // 执行初始化的相关操作
    init {
        if ((group == Tank.Group.ENEMY && type == Tank.Type.TYPE3)
                || (group != Tank.Group.ENEMY && type.ordinal > 0)) {
            speed = 8
        }
    }

    // 暂停坦克
    internal fun letStay() {
        speed = 0
    }

    // 恢复坦克移动
    internal fun letMove() {
        speed = if ((group == Tank.Group.ENEMY && type == Tank.Type.TYPE3)
                || (group != Tank.Group.ENEMY && type.ordinal > 0)) 8 else 4
    }


    // 负责将炮弹矩形化的函数
    internal fun getRect(): Rectangle {
        // 根据炮弹的方向进行分类讨论
        return when (direction) {
            // 当炮弹向上或者向下时
            Tank.Direction.UP, Tank.Direction.DOWN -> Rectangle(x - 3, y, 6, 8)
            // 当炮弹向左或者向右时
            Tank.Direction.LEFT, Tank.Direction.RIGHT -> Rectangle(x, y - 3, 8, 6)
        }
    }
}