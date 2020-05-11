package battlecity.view

import battlecity.stage.Grid
import battlecity.util.Animator
import java.awt.*
import java.awt.event.*
import java.lang.Runnable
import javax.swing.JButton
import javax.swing.JPanel

internal class MapEditor(private val client: Client) : JPanel(), Runnable, KeyListener, ActionListener {
    private var i = 32
    private var j = 48

    private var pressTime = 0
    private var flashTimer = 16
    private var delayTime = intArrayOf(3, 2)

    private var movable = BooleanArray(4)
    private var settable = false
    private var keepGrid = false
    private var displayBase = true
    private var editable = true

    private val buttons = arrayOf(JButton("Save"), JButton("Exit"))

    init {
        layout = null

        background = Color(0x7F7F7F)

        buttons[0].setBounds(150, 439, 70, 25)
        buttons[1].setBounds(260, 439, 70, 25)

        buttons.forEach {
            it.font = client.font14
            it.isFocusPainted = false
            it.addActionListener(this)
            add(it)
        }

        for (i in 23..25) {
            for (j in 11..14) {
                if (i == 23 || ((i == 24 || i == 25) && (j == 11 || j == 14))) {
                    City.stage.map[i][j] = Grid(j * 16 + 32, i * 16 + 16, Grid.Type.BRICK_WALL)
                }
            }
        }

        City.stage.map[24][12].type = Grid.Type.BASE
        City.stage.map[24][13].type = Grid.Type.BASE
        City.stage.map[25][12].type = Grid.Type.BASE
        City.stage.map[25][13].type = Grid.Type.BASE

        Thread(this).start()

        validate()
    }

    private fun complete() {
        for (i in 0..25) {
            for (j in 0..25) {
                if (City.stage.map[i][j].type == Grid.Type.WOOD) {
                    City.stage.woods.add(City.stage.map[i][j])
                }
            }
        }

        City.stage.map[24][12].type = Grid.Type.BASE
        City.stage.map[24][13].type = Grid.Type.BASE
        City.stage.map[25][12].type = Grid.Type.BASE
        City.stage.map[25][13].type = Grid.Type.BASE
    }

    // 绘制地图
    private fun drawMap(g: Graphics) {
        for (i in 0..25) {
            loop@for (j in 0..25) {
                when (City.stage.map[i][j].type) {
                    Grid.Type.BLANK, Grid.Type.BASE -> continue@loop
                    Grid.Type.BRICK_WALL -> g.drawImage(Animator.grid[2], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                    Grid.Type.STEEL_WALL -> g.drawImage(Animator.grid[3], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                    Grid.Type.RIVER -> g.drawImage(Animator.grid[4], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                    Grid.Type.WOOD -> g.drawImage(Animator.grid[6], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                    Grid.Type.ICE_FIELD -> g.drawImage(Animator.grid[7], City.stage.map[i][j].x, City.stage.map[i][j].y, 16, 16, null)
                }
            }
        }
    }

    private fun moveCursor() {
        when {
            movable[0] -> {
                if (delayTime[0] > 0) {
                    --delayTime[0]
                } else if (i > 48) {
                    i -= 32
                    delayTime[0] = 3
                }
            }
            movable[1] -> {
                if (delayTime[0] > 0) {
                    --delayTime[0]
                } else if (i < 400) {
                    i += 32
                    delayTime[0] = 3
                }
            }
            movable[2] -> {
                if (delayTime[0] > 0) {
                    --delayTime[0]
                } else if (j > 64) {
                    j -= 32
                    delayTime[0] = 3
                }
            }
            movable[3] -> {
                if (delayTime[0] > 0) {
                    --delayTime[0]
                } else if (j < 416) {
                    j += 32
                    delayTime[0] = 3
                }
            }
        }
    }

    private fun setElement() {
        if (settable) {
            if (delayTime[1] > 0) {
                --delayTime[1]
            } else {

                val row = i - 16 shr 4
                val column = j - 32 shr 4

                if (!keepGrid) {
                    if (pressTime < 13) {
                        ++pressTime
                    } else {
                        pressTime = 0
                    }
                }

                val type = when (pressTime) {
                    0 -> Grid.Type.BLANK
                    1, 2, 3, 4, 5 -> Grid.Type.BRICK_WALL
                    6, 7, 8, 9, 10 -> Grid.Type.STEEL_WALL
                    11 -> Grid.Type.RIVER
                    12 -> Grid.Type.WOOD
                    else -> Grid.Type.ICE_FIELD
                }

                when (pressTime) {
                    1, 6 -> {
                        City.stage.map[row - 1][column - 1] = Grid(j - 16, i - 16, Grid.Type.BLANK)
                        City.stage.map[row - 1][column] = Grid(j, i - 16, type)
                        City.stage.map[row][column - 1] = Grid(j - 16, i, Grid.Type.BLANK)
                        City.stage.map[row][column] = Grid(j, i, type)
                    }
                    2, 7 -> {
                        City.stage.map[row - 1][column - 1] = Grid(j - 16, i - 16, Grid.Type.BLANK)
                        City.stage.map[row - 1][column] = Grid(j, i - 16, Grid.Type.BLANK)
                        City.stage.map[row][column - 1] = Grid(j - 16, i, type)
                        City.stage.map[row][column] = Grid(j, i, type)
                    }
                    3, 8 -> {
                        City.stage.map[row - 1][column - 1] = Grid(j - 16, i - 16, type)
                        City.stage.map[row - 1][column] = Grid(j, i - 16, Grid.Type.BLANK)
                        City.stage.map[row][column - 1] = Grid(j - 16, i, type)
                        City.stage.map[row][column] = Grid(j, i, Grid.Type.BLANK)
                    }
                    4, 9 -> {
                        City.stage.map[row - 1][column - 1] = Grid(j - 16, i - 16, type)
                        City.stage.map[row - 1][column] = Grid(j, i - 16, type)
                        City.stage.map[row][column - 1] = Grid(j - 16, i, Grid.Type.BLANK)
                        City.stage.map[row][column] = Grid(j, i, Grid.Type.BLANK)
                    }
                    0, 5, 10, 11, 12, 13 -> {
                        City.stage.map[row - 1][column - 1] = Grid(j - 16, i - 16, type)
                        City.stage.map[row - 1][column] = Grid(j, i - 16, type)
                        City.stage.map[row][column - 1] = Grid(j - 16, i, type)
                        City.stage.map[row][column] = Grid(j, i, type)
                    }
                }

                if (displayBase && City.stage.map[25][13].type != Grid.Type.BASE) {
                    displayBase = false
                }

                keepGrid = true
                delayTime[1] = 3
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.color = Color.BLACK
        g.fillRect(32, 16, 416, 416)

        drawMap(g)

        if (displayBase) {
            g.drawImage(Animator.grid[0], 224, 400, 32, 32, null)
        }

        if (flashTimer in 0..8) {
            g.drawImage(Animator.playerImg[0][0][0], j - 13, i - 13, 26, 26, null)
        }

        if (City.gainFocus && --flashTimer < 0) {
            flashTimer = 16
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        if (e.source == buttons[0]) {
            complete()
            client.useCustomMap = true
        }
        editable = false
        client.startPanel = StartPanel(client)
        client.removeKeyListener(this)
        client.remove(client.mapEditor)
        client.add(client.startPanel)
        client.validate()
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_W -> {
                movable[0] = true
                keepGrid = true
            }
            KeyEvent.VK_S -> {
                movable[1] = true
                keepGrid = true
            }
            KeyEvent.VK_A -> {
                movable[2] = true
                keepGrid = true
            }
            KeyEvent.VK_D -> {
                movable[3] = true
                keepGrid = true
            }
            KeyEvent.VK_SPACE -> settable = true
        }
    }

    override fun keyReleased(e: KeyEvent) {
        when(e.keyCode) {
            KeyEvent.VK_W -> movable[0] = false
            KeyEvent.VK_S -> movable[1] = false
            KeyEvent.VK_A -> movable[2] = false
            KeyEvent.VK_D -> movable[3] = false
            KeyEvent.VK_SPACE -> {
                settable = false
                keepGrid = false
            }
        }
    }

    override fun keyTyped(e: KeyEvent) {}

    override fun run() {
        while (editable) {
            repaint()

            moveCursor()

            setElement()

            Thread.sleep(30L)
        }
    }
}