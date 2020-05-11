package battlecity.stage

// 元素类
internal open class Grid (val x: Int, val y: Int, var type: Type) {
    // 创建地图元素的类型
    internal enum class Type { BLANK, BRICK_WALL, STEEL_WALL, RIVER, WOOD, ICE_FIELD, BASE }
    // 创建地图的 5 个状态
    internal enum class Part { WHOLE, UP, BOTTOM, LEFT, RIGHT }
    // 地图元素默认为完整状态
    internal var part = Part.WHOLE
}