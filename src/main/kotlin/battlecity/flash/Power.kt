package battlecity.flash

import battlecity.util.Dynamic
import java.awt.Rectangle

// 道具类
internal class Power {
    // 创建道具类型
    internal enum class Type { Helmet, CLOCK, SHOVEL, STAR, BOMB, TANK }
    // 设置道具默认类型
    internal var type = Type.CLOCK
    // 设置道具坐标
    internal var x = 224
    internal var y = 208

    // 用于标记道具存在与否
    internal var exist = false
    // 定义计时器道具的效果时间
    internal var clockTime = 0
    // 定义铲子道具的效果时间
    internal var shovelTime = 0

    // 将道具矩形化
    internal fun getRect(): Rectangle {
        return Rectangle(x + 3, y + 2, 26, 26)
    }

    // 生成道具
    internal fun powerSpawn() {
        // 随机生成道具类型
        type = Dynamic.randomPower()
        // 随机生成道具坐标
        Dynamic.randomLocation()
        // 标记道具已存在
        exist = true
    }

    // 重置道具的相关参数
    internal fun onReset() {
        exist = false
        // 重置计时器道具的效果时间
        clockTime = 0
        // 重置铲子道具的效果时间
        shovelTime = 0
    }
}