package battlecity.util

import battlecity.movable.*
import battlecity.flash.*
import battlecity.stage.*
import battlecity.view.City
import java.awt.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import sun.audio.AudioPlayer

// 绘图类
internal object Animator {
    // 定义颜色
    private val orange = Color(0xFFA000)
    private val tangerine = Color(0xE05000)
    private val lightGray = Color(0x7F7F7F)

    // 定义字体
    private val gameFont = Font.createFont(Font.TRUETYPE_FONT, Animator::class.java.getResourceAsStream("/Fonts/Game.ttf")).deriveFont(21f)
    private val scoreFont = Font.createFont(Font.TRUETYPE_FONT, Animator::class.java.getResourceAsStream("/Fonts/Score.ttf")).deriveFont(20f)

    // 读取地图各元素图片
    internal val grid = arrayOf(
            getImage("Grid/Base1"), getImage("Grid/Base2"),
            getImage("Grid/Brick"), getImage("Grid/Steel"),
            getImage("Grid/River1"), getImage("Grid/River2"),
            getImage("Grid/Woods"), getImage("Grid/IceField"))
    // 创建砖墙各部分图片
    private val subBrick = arrayOf(
            grid[2].getSubimage(0, 0, 16, 8), grid[2].getSubimage(0, 8, 16, 8),
            grid[2].getSubimage(0, 0, 8, 16), grid[2].getSubimage(8, 0, 8, 16))

    // 读取坦克生成器图片
    private val spawn = arrayOf(
            getImage("Spawn/1"), getImage("Spawn/2"),
            getImage("Spawn/3"), getImage("Spawn/4"))

    // 读取坦克防护罩图片
    private val shield = arrayOf(
            getImage("Shield/1"), getImage("Shield/2"))

    // 读取炮弹图片
    private val shell = arrayOf(
            getImage("Shell/Up"), getImage("Shell/Down"),
            getImage("Shell/Left"), getImage("Shell/Right"))

    // 读取爆炸图片
    private val explosion = arrayOf(
            getImage("Explosion/1"), getImage("Explosion/2"),
            getImage("Explosion/3"), getImage("Explosion/4"),
            getImage("Explosion/5"))

    // 读取道具图片
    private val powerUp = arrayOf(
            getImage("Power/Helmet"), getImage("Power/Clock"),
            getImage("Power/Shovel"), getImage("Power/Star"),
            getImage("Power/Bomb"), getImage("Power/Tank"))

    // 读取标志图片
    private val sign = arrayOf(
            getImage("Sign/Enemy"), getImage("Sign/Player"),
            getImage("Sign/Flag"), getImage("Sign/GameOver"))

    // 读取敌方普通类型坦克图片
    private val normalEnemyImg = arrayOf(
            arrayOf(getImage("Enemy/Normal/Type1/Up1"), getImage("Enemy/Normal/Type1/Up2"),
                    getImage("Enemy/Normal/Type1/Down1"), getImage("Enemy/Normal/Type1/Down2"),
                    getImage("Enemy/Normal/Type1/Left1"), getImage("Enemy/Normal/Type1/Left2"),
                    getImage("Enemy/Normal/Type1/Right1"), getImage("Enemy/Normal/Type1/Right2")),
            arrayOf(getImage("Enemy/Normal/Type2/Up1"), getImage("Enemy/Normal/Type2/Up2"),
                    getImage("Enemy/Normal/Type2/Down1"), getImage("Enemy/Normal/Type2/Down2"),
                    getImage("Enemy/Normal/Type2/Left1"), getImage("Enemy/Normal/Type2/Left2"),
                    getImage("Enemy/Normal/Type2/Right1"), getImage("Enemy/Normal/Type2/Right2")),
            arrayOf(getImage("Enemy/Normal/Type3/Up1"), getImage("Enemy/Normal/Type3/Up2"),
                    getImage("Enemy/Normal/Type3/Down1"), getImage("Enemy/Normal/Type3/Down2"),
                    getImage("Enemy/Normal/Type3/Left1"), getImage("Enemy/Normal/Type3/Left2"),
                    getImage("Enemy/Normal/Type3/Right1"), getImage("Enemy/Normal/Type3/Right2")),
            arrayOf(getImage("Enemy/Normal/Type4/Color1/Up1"), getImage("Enemy/Normal/Type4/Color1/Up2"),
                    getImage("Enemy/Normal/Type4/Color1/Down1"), getImage("Enemy/Normal/Type4/Color1/Down2"),
                    getImage("Enemy/Normal/Type4/Color1/Left1"), getImage("Enemy/Normal/Type4/Color1/Left2"),
                    getImage("Enemy/Normal/Type4/Color1/Right1"), getImage("Enemy/Normal/Type4/Color1/Right2")),
            arrayOf(getImage("Enemy/Normal/Type4/Color2/Up1"), getImage("Enemy/Normal/Type4/Color2/Up2"),
                    getImage("Enemy/Normal/Type4/Color2/Down1"), getImage("Enemy/Normal/Type4/Color2/Down2"),
                    getImage("Enemy/Normal/Type4/Color2/Left1"), getImage("Enemy/Normal/Type4/Color2/Left2"),
                    getImage("Enemy/Normal/Type4/Color2/Right1"), getImage("Enemy/Normal/Type4/Color2/Right2")),
            arrayOf(getImage("Enemy/Normal/Type4/Color3/Up1"), getImage("Enemy/Normal/Type4/Color3/Up2"),
                    getImage("Enemy/Normal/Type4/Color3/Down1"), getImage("Enemy/Normal/Type4/Color3/Down2"),
                    getImage("Enemy/Normal/Type4/Color3/Left1"), getImage("Enemy/Normal/Type4/Color3/Left2"),
                    getImage("Enemy/Normal/Type4/Color3/Right1"), getImage("Enemy/Normal/Type4/Color3/Right2")))

    // 读取敌方闪光类型坦克图片
    private val flashEnemyImg = arrayOf(
            arrayOf(getImage("Enemy/Flash/Type1/Up1"), getImage("Enemy/Flash/Type1/Up2"),
                    getImage("Enemy/Flash/Type1/Down1"), getImage("Enemy/Flash/Type1/Down2"),
                    getImage("Enemy/Flash/Type1/Left1"), getImage("Enemy/Flash/Type1/Left2"),
                    getImage("Enemy/Flash/Type1/Right1"), getImage("Enemy/Flash/Type1/Right2")),
            arrayOf(getImage("Enemy/Flash/Type2/Up1"), getImage("Enemy/Flash/Type2/Up2"),
                    getImage("Enemy/Flash/Type2/Down1"), getImage("Enemy/Flash/Type2/Down2"),
                    getImage("Enemy/Flash/Type2/Left1"), getImage("Enemy/Flash/Type2/Left2"),
                    getImage("Enemy/Flash/Type2/Right1"), getImage("Enemy/Flash/Type2/Right2")),
            arrayOf(getImage("Enemy/Flash/Type3/Up1"), getImage("Enemy/Flash/Type3/Up2"),
                    getImage("Enemy/Flash/Type3/Down1"), getImage("Enemy/Flash/Type3/Down2"),
                    getImage("Enemy/Flash/Type3/Left1"), getImage("Enemy/Flash/Type3/Left2"),
                    getImage("Enemy/Flash/Type3/Right1"), getImage("Enemy/Flash/Type3/Right2")),
            arrayOf(getImage("Enemy/Flash/Type4/Up1"), getImage("Enemy/Flash/Type4/Up2"),
                    getImage("Enemy/Flash/Type4/Down1"), getImage("Enemy/Flash/Type4/Down2"),
                    getImage("Enemy/Flash/Type4/Left1"), getImage("Enemy/Flash/Type4/Left2"),
                    getImage("Enemy/Flash/Type4/Right1"), getImage("Enemy/Flash/Type4/Right2")))

    // 读取玩家坦克图片
    internal val playerImg = arrayOf(
            arrayOf(arrayOf(getImage("Player/Player1/Type1/Up1"), getImage("Player/Player1/Type1/Up2"),
                            getImage("Player/Player1/Type1/Down1"), getImage("Player/Player1/Type1/Down2"),
                            getImage("Player/Player1/Type1/Left1"), getImage("Player/Player1/Type1/Left2"),
                            getImage("Player/Player1/Type1/Right1"), getImage("Player/Player1/Type1/Right2")),
                    arrayOf(getImage("Player/Player1/Type2/Up1"), getImage("Player/Player1/Type2/Up2"),
                            getImage("Player/Player1/Type2/Down1"), getImage("Player/Player1/Type2/Down2"),
                            getImage("Player/Player1/Type2/Left1"), getImage("Player/Player1/Type2/Left2"),
                            getImage("Player/Player1/Type2/Right1"), getImage("Player/Player1/Type2/Right2")),
                    arrayOf(getImage("Player/Player1/Type3/Up1"), getImage("Player/Player1/Type3/Up2"),
                            getImage("Player/Player1/Type3/Down1"), getImage("Player/Player1/Type3/Down2"),
                            getImage("Player/Player1/Type3/Left1"), getImage("Player/Player1/Type3/Left2"),
                            getImage("Player/Player1/Type3/Right1"), getImage("Player/Player1/Type3/Right2")),
                    arrayOf(getImage("Player/Player1/Type4/Up1"), getImage("Player/Player1/Type4/Up2"),
                            getImage("Player/Player1/Type4/Down1"), getImage("Player/Player1/Type4/Down2"),
                            getImage("Player/Player1/Type4/Left1"), getImage("Player/Player1/Type4/Left2"),
                            getImage("Player/Player1/Type4/Right1"), getImage("Player/Player1/Type4/Right2"))),
            arrayOf(arrayOf(getImage("Player/Player2/Type1/Up1"), getImage("Player/Player2/Type1/Up2"),
                            getImage("Player/Player2/Type1/Down1"), getImage("Player/Player2/Type1/Down2"),
                            getImage("Player/Player2/Type1/Left1"), getImage("Player/Player2/Type1/Left2"),
                            getImage("Player/Player2/Type1/Right1"), getImage("Player/Player2/Type1/Right2")),
                    arrayOf(getImage("Player/Player2/Type2/Up1"), getImage("Player/Player2/Type2/Up2"),
                            getImage("Player/Player2/Type2/Down1"), getImage("Player/Player2/Type2/Down2"),
                            getImage("Player/Player2/Type2/Left1"), getImage("Player/Player2/Type2/Left2"),
                            getImage("Player/Player2/Type2/Right1"), getImage("Player/Player2/Type2/Right2")),
                    arrayOf(getImage("Player/Player2/Type3/Up1"), getImage("Player/Player2/Type3/Up2"),
                            getImage("Player/Player2/Type3/Down1"), getImage("Player/Player2/Type3/Down2"),
                            getImage("Player/Player2/Type3/Left1"), getImage("Player/Player2/Type3/Left2"),
                            getImage("Player/Player2/Type3/Right1"), getImage("Player/Player2/Type3/Right2")),
                    arrayOf(getImage("Player/Player2/Type4/Up1"), getImage("Player/Player2/Type4/Up2"),
                            getImage("Player/Player2/Type4/Down1"), getImage("Player/Player2/Type4/Down2"),
                            getImage("Player/Player2/Type4/Left1"), getImage("Player/Player2/Type4/Left2"),
                            getImage("Player/Player2/Type4/Right1"), getImage("Player/Player2/Type4/Right2"))))


    // 定义游戏开始倒计时和结束倒计时
    internal var countdown = intArrayOf(94, 135)
    // 定义“GameOver"文字的坐标
    internal var gameOverXY = intArrayOf(-13, 429, 514)
    // 定义游戏图像闪烁用的定时器
    private val flashTimer = intArrayOf(4, 16, 32, 64)

    // 定义得分榜用于控制文字变化的时间间隔
    internal var interval = intArrayOf(41, 143)
    // 定义得分榜各文字是否开始绘制的标志量
    private var drawable = booleanArrayOf(false, false, false, false, true)


    // 绘制关卡开始界面
    fun drawStageCover(g: Graphics) {
        if (countdown[0] > 0) {
            when (countdown[0]) {
                in 15..94 -> {
                    // 绘制界面背景颜色
                    g.color = lightGray
                    g.fillRect(0, 0, 512, 448)

                    // 绘制关卡编号
                    g.font = gameFont
                    g.color = Color.BLACK
                    g.drawString("STAGE", 193, 222)
                    drawNumber("${Config.stageIndex}", 321, 222, g)
                }
                in 1..14 -> {
                    // 绘制界面消失效果
                    g.color = lightGray
                    g.fillRect(0, 0, 512, countdown[0] shl 4)
                    g.fillRect(0, 448 - (countdown[0] shl 4), 512, countdown[0] shl 4)
                }
            }
            if (City.gainFocus) {
                --countdown[0]
            }
        }
    }

    // 绘制信息面板
    fun drawInfo(g: Graphics) {
        g.color = Color.BLACK

        for (i in 0 until (Config.enemySum shr 1)) {
            g.drawImage(sign[0], 466, 34 + (i shl 4), 14, 14, null)
            g.drawImage(sign[0], 482, 34 + (i shl 4), 14, 14, null)
        }

        if (Config.enemySum % 2 != 0) {
            g.drawImage(sign[0], 466, 34 + (Config.enemySum shr 1 shl 4), 14, 14, null)
        }

        g.font = gameFont

        g.drawString("ⅠP", 467, 270)
        if (City.players[0].health < 10) {
            g.drawImage(sign[1], 466, 272, 14, 16, null)
        }
        drawNumber("${City.players[0].health}", 497, 286, g)

        if (City.players.size == 2) {
            g.drawString("ⅡP", 467, 318)
            if (City.players[1].health < 10) {
                g.drawImage(sign[1], 466, 320, 14, 16, null)
            }
            drawNumber("${City.players[1].health}", 497, 334, g)
        }

        g.drawImage(sign[2], 464, 352, 32, 30, null)
        drawNumber("${Config.stageIndex}", 497, 398, g)
        g.fillRect(32, 16, 416, 416)
    }


    // 绘制树林
    internal fun drawWood(g: Graphics) {
        City.stage.woods.forEach {
            g.drawImage(grid[6], it.x, it.y, 16, 16, null)
        }
    }

    // 绘制基地
    internal fun drawBase(g: Graphics) {
        if (City.destroy) {
            g.drawImage(grid[1], 224, 400, 32, 32, null)
        } else {
            g.drawImage(grid[0], 224, 400, 32, 32, null)
        }

        if (City.power.shovelTime > 0 && !City.pause && City.gainFocus) {
            if (City.power.shovelTime in 1..193 step 32) {
                City.stage.changeBaseWallToBrick()
            } else if (City.power.shovelTime in 17..177 step 32) {
                City.stage.changeBaseWallToSteel()
            }

            --City.power.shovelTime
        }
    }

    // 绘制砖墙
    private fun drawBrickWall(wall: Grid, g: Graphics) {
        when (wall.part) {
            Grid.Part.WHOLE -> g.drawImage(grid[2], wall.x, wall.y, 16, 16, null)
            Grid.Part.UP -> g.drawImage(subBrick[0], wall.x, wall.y, 16, 8, null)
            Grid.Part.BOTTOM -> g.drawImage(subBrick[1], wall.x, wall.y + 8, 16, 8, null)
            Grid.Part.LEFT -> g.drawImage(subBrick[2], wall.x, wall.y, 8, 16, null)
            Grid.Part.RIGHT -> g.drawImage(subBrick[3], wall.x + 8, wall.y, 8, 16, null)
        }
    }

    // 绘制地图
    internal fun drawMap(g: Graphics) {
        for (i in 0..25) {
            loop@ for (j in 0..25) {
                when (City.stage.map[i][j].type) {
                    Grid.Type.BLANK, Grid.Type.BASE, Grid.Type.WOOD-> continue@loop
                    Grid.Type.BRICK_WALL -> drawBrickWall(City.stage.map[i][j], g)
                    Grid.Type.STEEL_WALL -> g.drawImage(grid[3], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                    Grid.Type.RIVER -> {
                        if (flashTimer[3] in 33..64) {
                            g.drawImage(grid[4], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                        } else {
                            g.drawImage(grid[5], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                        }
                    }
                    Grid.Type.ICE_FIELD -> g.drawImage(grid[7], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                }
            }
        }
    }

    // 绘制坦克生成器
    private fun drawSpawn(spawn: Spawn, g: Graphics) {
        if (countdown[0] == 0) {
            when (spawn.existTime) {
                in 53..56, in 25..30, in 1..2 -> g.drawImage(
                    this.spawn[0],
                    spawn.x - 15,
                    spawn.y - 15,
                    30,
                    30,
                    null
                )
                in 49..52, in 21..24 -> g.drawImage(this.spawn[1], spawn.x - 13, spawn.y - 13, 26, 26, null)
                in 45..48, in 17..20 -> g.drawImage(this.spawn[2], spawn.x - 11, spawn.y - 11, 22, 22, null)
                in 39..44, in 11..16 -> g.drawImage(this.spawn[3], spawn.x - 9, spawn.y - 9, 18, 18, null)
                in 35..38, in 7..10 -> g.drawImage(this.spawn[2], spawn.x - 11, spawn.y - 11, 22, 22, null)
                in 31..34, in 3..6 -> g.drawImage(this.spawn[1], spawn.x - 13, spawn.y - 13, 26, 26, null)
            }

            if (!City.pause && City.gainFocus) {
                if (spawn.existTime == 56) {
                    spawn.checkLocation()
                }
                --spawn.existTime
            }
        }
    }

    // 绘制坦克
    private fun drawTank(tank: Tank, tankImg: Array<BufferedImage>, g: Graphics) {
        if (flashTimer[0] in 3..4 && tank.moving) {
            when (tank.direction) {
                Tank.Direction.UP -> g.drawImage(tankImg[0], tank.x - tank.halfW, tank.y - tank.halfH, tank.halfW shl 1, tank.halfH shl 1, null)
                Tank.Direction.DOWN -> g.drawImage(tankImg[2], tank.x - tank.halfW, tank.y - tank.halfH, tank.halfW shl 1, tank.halfH shl 1, null)
                Tank.Direction.LEFT -> g.drawImage(tankImg[4], tank.x - tank.halfH, tank.y - tank.halfW, tank.halfH shl 1, tank.halfW shl 1, null)
                Tank.Direction.RIGHT -> g.drawImage(tankImg[6], tank.x - tank.halfH, tank.y - tank.halfW, tank.halfH shl 1, tank.halfW shl 1, null)
            }
        } else {
            when (tank.direction) {
                Tank.Direction.UP -> g.drawImage(tankImg[1], tank.x - tank.halfW, tank.y - tank.halfH, tank.halfW shl 1, tank.halfH shl 1, null)
                Tank.Direction.DOWN -> g.drawImage(tankImg[3], tank.x - tank.halfW, tank.y - tank.halfH, tank.halfW shl 1, tank.halfH shl 1, null)
                Tank.Direction.LEFT -> g.drawImage(tankImg[5], tank.x - tank.halfH, tank.y - tank.halfW, tank.halfH shl 1, tank.halfW shl 1, null)
                Tank.Direction.RIGHT -> g.drawImage(tankImg[7], tank.x - tank.halfH, tank.y - tank.halfW, tank.halfH shl 1, tank.halfW shl 1, null)
            }
        }
    }

    // 绘制敌方坦克
    internal fun drawEnemy(g: Graphics) {
        if (City.enemies.size < Config.maxCount) {
            if (Config.enemySum > 0 && Enemy.spawn.existTime > 0) {
                drawSpawn(Enemy.spawn, g)
            } else if (City.gainFocus && City.enemies.isEmpty() && countdown[1] > 0) {
                --countdown[1]
            }
        }

        City.enemies.forEach {
            if (it.alive) {
                if (it.flashing) {
                    if (flashTimer[1] in 9..16) {
                        drawTank(it, flashEnemyImg[it.type.ordinal], g)
                    } else {
                        drawTank(it, normalEnemyImg[it.type.ordinal], g)
                    }
                } else {
                    if (it.type != Tank.Type.TYPE4) {
                        drawTank(it, normalEnemyImg[it.type.ordinal], g)
                    } else {
                        when (it.health) {
                            3 -> if (flashTimer[0] in 1..4 step 2) drawTank(it, normalEnemyImg[5], g) else drawTank(it, normalEnemyImg[3], g)
                            2 -> if (flashTimer[0] in 1..4 step 2) drawTank(it, normalEnemyImg[4], g) else drawTank(it, normalEnemyImg[3], g)
                            1 -> if (flashTimer[0] in 1..4 step 2) drawTank(it, normalEnemyImg[4], g) else drawTank(it, normalEnemyImg[5], g)
                            0 -> drawTank(it, normalEnemyImg[3], g)
                        }
                    }
                }
            } else {
                City.enemies.remove(it)
            }
        }
    }

    // 绘制玩家坦克
    internal fun drawPlayer(g: Graphics) {
        City.players.forEach {
            if (it.spawn.existTime > 0) {
                drawSpawn(it.spawn, g)
            } else if (it.alive) {
                if (!it.flashing || it.flashTime !in 1..33 step 2) {
                    drawTank(it, playerImg[it.group.ordinal][it.type.ordinal], g)
                }

                if (it.helmetTime > 0 && !City.pause) {
                    when (it.helmetTime) {
                        in 1..611 step 4, in 2..612 step 4 ->
                            g.drawImage(shield[0], it.x - 16, it.y - 16, 32, 32, null)
                        in 3..609 step 4, in 4..610 step 4 ->
                            g.drawImage(shield[1], it.x - 16, it.y - 16, 32, 32, null)
                    }

                    if (City.gainFocus) {
                        --it.helmetTime
                    }
                }

                if (it.flashing && it.flashTime > 0) {
                    if (City.gainFocus && (flashTimer[1] == 8 || flashTimer[1] == 16)) {
                        --it.flashTime
                    }
                } else {
                    if (!City.pause) {
                        it.flashing = false
                    }
                    it.flashTime = 33
                }
            }
        }
    }

    // 绘制炮弹
    fun drawShell(g: Graphics) {
        City.shells.forEach {
            if (it.alive) {
                when (it.direction) {
                    Tank.Direction.UP -> g.drawImage(shell[0], it.x - 3, it.y, 6, 8, null)
                    Tank.Direction.DOWN -> g.drawImage(shell[1], it.x - 3, it.y, 6, 8, null)
                    Tank.Direction.LEFT -> g.drawImage(shell[2], it.x, it.y - 3, 8, 6, null)
                    Tank.Direction.RIGHT -> g.drawImage(shell[3], it.x, it.y - 3, 8, 6, null)
                }
            } else {
                City.shells.remove(it)
            }
        }
    }

    // 绘制爆炸
    fun drawExplosion(g: Graphics) {
        City.explosions.forEach {
            if (it.existTime != 0) {
                when (it.existTime) {
                    in 43..48, in -9..-7 -> g.drawImage(explosion[0], it.x - 11, it.y - 11, 22, 22, null)
                    in 37..42, in -6..-4 -> g.drawImage(explosion[1], it.x - 15, it.y - 14, 30, 28, null)
                    in 31..36, in 13..18, in -3..-1 -> g.drawImage(explosion[2], it.x - 16, it.y - 16, 32, 32, null)
                    in 25..30 -> g.drawImage(explosion[3], it.x - 32, it.y - 29, 64, 58, null)
                    in 19..24 -> g.drawImage(explosion[4], it.x - 32, it.y - 31, 64, 62, null)
                }
                if (!City.pause && City.gainFocus) {
                    when {
                        it.existTime > 0 -> --it.existTime
                        it.existTime < 0 -> ++it.existTime
                    }
                }
            } else {
                City.explosions.remove(it)
            }
        }

    }

    // 绘制分数
    fun drawScore(g: Graphics) {
        City.scores.forEach {
            if (it.existTime > 0) {
                g.font = scoreFont
                g.color = Color.WHITE

                g.drawString("${it.type}00", it.x, it.y)

                if (!City.pause && City.gainFocus) {
                    --it.existTime
                }
            } else {
                City.scores.remove(it)
            }
        }
    }

    // 绘制道具
    fun drawPower(g: Graphics) {
        if (City.power.exist && flashTimer[1] in 9..16) {
            g.drawImage(powerUp[City.power.type.ordinal], City.power.x, City.power.y, 32, 30, null)
        }
    }


    // 绘制暂停符号
    fun drawPauseSign(g: Graphics) {
        if (City.pause && flashTimer[2] in 17..32) {
            g.font = gameFont
            g.color = tangerine
            g.drawString("PAUSE", 201, 240)
        }
    }

    // 绘制GameOver符号
    fun drawGameOverSign(g: Graphics) {
        // 当游戏为双人模式时
        if (City.players.size == 2) {
            if (!City.players[0].alive && City.players[1].alive && gameOverXY[0] <= 433) {
                g.font = gameFont
                g.color = tangerine
                if (gameOverXY[0] in 51..145) {
                    g.drawString("GAME", gameOverXY[0], 416)
                    g.drawString("OVER", gameOverXY[0], 432)
                } else if (gameOverXY[0] in 147..433) {
                    g.drawString("GAME", 145, 416)
                    g.drawString("OVER", 145, 432)
                }

                if (!City.pause && City.gainFocus) {
                    gameOverXY[0] += 2
                }
            }

            if (City.players[0].alive && !City.players[1].alive && gameOverXY[1] >= -15) {
                g.font = gameFont
                g.color = tangerine

                if (gameOverXY[1] in 273..367) {
                    g.drawString("GAME", gameOverXY[1], 416)
                    g.drawString("OVER", gameOverXY[1], 432)
                } else if (gameOverXY[0] in -15..271 ) {
                    g.drawString("GAME", 273, 416)
                    g.drawString("OVER", 273, 432)
                }

                if (!City.pause && City.gainFocus) {
                    gameOverXY[1] -= 2
                }
            }
        }

        if (City.destroy || City.players.size == 1 && !City.players[0].alive
                || City.players.size == 2 && !City.players[0].alive && !City.players[1].alive) {
            g.font = gameFont
            g.color = tangerine

            g.drawString("GAME", 209, gameOverXY[2])
            g.drawString("OVER", 209, gameOverXY[2] + 16)

            if (gameOverXY[2] > 210) {
                if (!City.pause && City.gainFocus) {
                    gameOverXY[2] -= 2
                }
            } else if (City.gainFocus && countdown[1] > 0) {
                --countdown[1]
            }
        }
    }
    
    // 绘制奖励文字
    private fun drawBonus(player: Player, x: Int, g: Graphics) {
        g.color = tangerine
        g.drawString("BONUS!", x, 398)
        g.color = Color.WHITE
        g.drawString("1000 PTS", x, 414)

        if (interval[1] == 120) {
            player.scoreTotal += 1000
            AudioPlayer.player.start(Animator::class.java.getResourceAsStream("/Audio/Bonus.au"))
        }
    }
    // 绘制分数动态效果
    private fun drawPoint(player: Player, otherPoint: Int, ordinal: Int, pointX: Int, scoreX: Int, y: Int, g: Graphics) {
        if (player.counter[ordinal] <= player.points[ordinal]) {
            if ((player.points[ordinal] > otherPoint || player.points[ordinal] == otherPoint && player.group.ordinal == 1)
                    && player.interval[0] == 9) {
                AudioPlayer.player.start(Animator::class.java.getResourceAsStream("/Audio/AddPoint.au"))
            }

            drawNumber("${player.counter[ordinal]}", pointX, y, g)
            drawNumber("${player.counter[ordinal] * (ordinal + 1) * 100}", scoreX, y, g)

            if (City.gainFocus && --player.interval[0] < 0) {
                player.interval[0] = 9
                player.counter[ordinal]++
            }
        } else {
            drawNumber("${player.points[ordinal]}", pointX, y, g)
            drawNumber("${player.scores[ordinal]}", scoreX, y, g)

            if (!drawable[ordinal] && City.gainFocus
                    && (player.points[ordinal] >= otherPoint || player.points[ordinal] == otherPoint && player.group.ordinal == 1)
                    && --player.interval[1] < 0) {
                player.interval[1] = 30
                drawable[ordinal] = true
            }
        }
    }
    
    private fun drawSign(x1: Int, x2: Int, g: Graphics) {
        g.drawString("PTS", x1, 190)
        g.drawString("PTS", x1, 238)
        g.drawString("PTS", x1, 286)
        g.drawString("PTS", x1, 334)
        g.drawString("←", x2, 190)
        g.drawString("←", x2, 238)
        g.drawString("←", x2, 286)
        g.drawString("←", x2, 334)
    }
    // 绘制得分榜
    internal fun drawScoreBoard(g: Graphics) {
        if (drawable[4]) {
            g.font = gameFont
            g.color = tangerine
            g.drawString("HI-SCORE", 129, 46)
            g.drawString("Ⅰ-PLAYER", 51, 110)

            g.color = orange
            drawNumber("${Config.hiScore}", 385, 46, g)
            drawNumber("${City.players[0].scoreTotal}", 177, 142, g)

            g.color = Color.WHITE
            g.drawString("STAGE", 193, 78)
            g.drawString("${Config.stageIndex}", 305, 78)
            drawSign(129, 225, g)
            g.drawString("TOTAL", 98, 366)

            if (City.players.size == 2) {
                g.font = gameFont
                g.color = tangerine
                g.drawString("Ⅱ-PLAYER", 339, 110)
                g.color = orange
                drawNumber("${City.players[1].scoreTotal}", 465, 142, g)
                g.color = Color.WHITE
                drawSign(417, 273, g)
            }

            g.drawImage(normalEnemyImg[0][0], 244, 170, 26, 30, null)
            g.drawImage(normalEnemyImg[1][0], 244, 218, 26, 30, null)
            g.drawImage(normalEnemyImg[2][0], 244, 266, 26, 30, null)
            g.drawImage(normalEnemyImg[3][0], 244, 314, 26, 30, null)

            if (City.gainFocus && interval[0] > 0) {
                --interval[0]
            } else {
                if (City.players.size == 1) {
                        drawPoint(City.players[0], 0, 0, 225, 113, 190, g)
                    if (drawable[0]) {
                        drawPoint(City.players[0], 0, 1, 225, 113, 238, g)
                    }
                    if (drawable[1]) {
                        drawPoint(City.players[0], 0, 2, 225, 113, 286, g)
                    }
                    if (drawable[2]) {
                        drawPoint(City.players[0], 0, 3, 225, 113, 334, g)
                    }
                } else {
                        drawPoint(City.players[0], City.players[1].points[0], 0, 225, 113, 190, g)
                        drawPoint(City.players[1], City.players[0].points[0], 0, 321, 401, 190, g)
                    if (drawable[0]) {
                        drawPoint(City.players[0], City.players[1].points[1], 1, 225, 113, 238, g)
                        drawPoint(City.players[1], City.players[0].points[1], 1, 321, 401, 238, g)
                    }
                    if (drawable[1]) {
                        drawPoint(City.players[0], City.players[1].points[2], 2, 225, 113, 286, g)
                        drawPoint(City.players[1], City.players[0].points[2], 2, 321, 401, 286, g)
                    }
                    if (drawable[2]) {
                        drawPoint(City.players[0], City.players[1].points[3], 3, 225, 113, 334, g)
                        drawPoint(City.players[1], City.players[0].points[3], 3, 321, 401, 334, g)
                    }
                }

                if (drawable[3]) {
                    if (City.gainFocus && interval[1] > 0) {
                        --interval[1]
                    } else if (City.destroy || City.players.size == 1 && !City.players[0].alive
                            || City.players.size == 2 && !City.players[0].alive && !City.players[1].alive) {
                        drawable[4] = false
                        AudioPlayer.player.start(Animator::class.java.getResourceAsStream("/Audio/GameOver.au"))
                    }

                    if (interval[1] < 136) {
                        drawNumber("${City.players[0].points[4]}", 225, 366, g)

                        if (City.players.size == 2) {
                            drawNumber("${City.players[1].points[4]}", 321, 366, g)

                            if (!City.destroy && interval[1] < 121) {
                                if (City.players[0].alive && City.players[0].points[4] > City.players[1].points[4]) {
                                    drawBonus(City.players[0], 49, g)
                                } else if (City.players[1].alive && City.players[0].points[4] < City.players[1].points[4]) {
                                    drawBonus(City.players[1], 353, g)
                                }
                            }
                        }
                    }
                }
            }

            (g as Graphics2D).stroke = BasicStroke(4f)
            g.drawLine(194, 348, 318, 348)
        } else {
            g.drawImage(sign[3], 128, 128, 248, 160, null)
        }
    }


    fun flash() {
        // 控制坦克履带的转动和3号类型敌方坦克的闪烁
        if (--flashTimer[0] == 0) {
            flashTimer[0] = 4
        }

        // 控制道具坦克和道具的闪烁
        if (City.gainFocus && --flashTimer[1] == 0) {
            flashTimer[1] = 16
        }

        // 控制暂停符号的闪烁
        if (City.pause && City.gainFocus && --flashTimer[2] == 0) {
            flashTimer[2] = 32
        }

        // 控制河流图片的切换
        if (!City.pause && City.gainFocus && --flashTimer[3] == 0) {
            flashTimer[3] = 64
        }
    }

    // 重置相关变量
    internal fun onReset() {
        countdown = intArrayOf(94, 135)
        interval = intArrayOf(41, 143)
        drawable = booleanArrayOf(false, false, false, false, true)
    }

    // 获取图片
    internal fun getImage(path: String): BufferedImage {
        return ImageIO.read(Animator::class.java.getResourceAsStream("/Images/$path.png"))
    }

    // 绘制数字
    private fun drawNumber(string: String, x: Int, y: Int, g: Graphics) {
        g.drawString(string, x - g.fontMetrics.stringWidth(string), y)
    }
}