package battlecity.view

import battlecity.util.Animator
import battlecity.util.Config
import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.*

// 游戏“Setting”对话框类
internal class Setting(client: Client) : JDialog(), ItemListener, ActionListener {
    // 创建文本框的背景颜色
    private val color = Color(0xD3D3D3)
    // 创建关卡预览标签
    private val previewStage = JLabel(ImageIcon(Animator.getImage("Stage/STAGE01")))
    // 创建下拉列表
    private val stageList: JComboBox<String> = JComboBox()
    // 创建文件标签
    private val labels = arrayOf(
            JLabel("Key Map"), JLabel("Player1"),
            JLabel("Player2"))
    // 创建玩家 1 的按键文本框
    private val controller1: Array<JTextField> = arrayOf(
            JTextField("W"), JTextField("S"),
            JTextField("A"), JTextField("D"),
            JTextField("Space"))
    // 创建玩家 2 的按键文本框
    private val controller2: Array<JTextField> = arrayOf(
            JTextField("Num 8"), JTextField("Num 5"),
            JTextField("Num 4"), JTextField("Num 6"),
            JTextField("Ctrl"))
    // 创建确认按钮
    private val confirm = JButton("OK")

    // 执行初始化的相关操作
    init {
        // 设置关卡预览标签的位置和大小
        previewStage.setBounds(2, 2, 332, 308)
        // 将关卡预览标签边框设置为凹陷效果
        previewStage.border = BorderFactory.createLoweredBevelBorder()
        // 将关卡预览标签添加到设置窗口
        add(previewStage)

        // 初始化下拉列表
        for (i in 1..35) {
            if (i in 1..9) {
                stageList.addItem("STAGE0$i")
            } else {
                stageList.addItem("STAGE$i")
            }
        }
        // 设置下拉列表的背景颜色
        stageList.background = color
        // 监听下拉列表的点击事件
        stageList.addItemListener(this)
        // 将下拉列表边框设置为凹陷效果
        stageList.border = BorderFactory.createRaisedBevelBorder()
        // 设置下拉列表的位置和大小
        stageList.setBounds(126, 327, 84, 25)
        // 将下拉列表添加到设置窗口
        add(stageList)


        // 设置文字标签的字体、位置和大小
        labels[0].font = client.font16
        labels[0].setBounds(133, 365, 80, 20)
        labels[1].font = client.font14
        labels[1].setBounds(56, 390, 60, 20)
        labels[2].font = client.font14
        labels[2].setBounds(224, 390, 60, 24)
        labels.forEach { add(it) }

        // 设置玩家 1 的按键文本框
        controller1.forEach {
            // 设置按键文本框的背景颜色
            it.background = color
            // 将按键文本框设为不可点击
            it.isEditable = false
            // 将按键文本框的字体设为居中
            it.horizontalAlignment = JTextField.CENTER
            // 将按键文本框的边框设为突起
            it.border = BorderFactory.createRaisedBevelBorder()
            // 将按键文本框添加到设置窗口
            add(it)
        }
        // 设置玩家 2 的按键文本框
        controller2.forEach {
            // 设置按键文本框的背景颜色
            it.background = color
            // 将按键文本框设为不可点击
            it.isEditable = false
            // 将按键文本框的字体设为居中
            it.horizontalAlignment = JTextField.CENTER
            // 将按键文本框的边框设为突起
            it.border = BorderFactory.createRaisedBevelBorder()
            // 将按键文本框添加到设置窗口
            add(it)
        }
        // 设置按键标签的位置和大小
        controller1[0].setBounds(54, 412, 60, 24)
        controller1[1].setBounds(54, 492, 60, 24)
        controller1[2].setBounds(16, 452, 60, 24)
        controller1[3].setBounds(92, 452, 60, 24)
        controller1[4].setBounds(92, 532, 60, 24)
        controller2[0].setBounds(222, 412, 60, 24)
        controller2[1].setBounds(222, 492, 60, 24)
        controller2[2].setBounds(184, 452, 60, 24)
        controller2[3].setBounds(260, 452, 60, 24)
        controller2[4].setBounds(184, 532, 60, 24)


        // 设置按钮的字体
        confirm.font = client.font16
        confirm.isFocusPainted = false
        // 为按钮注册ActionEvent监视器
        confirm.addActionListener(this)
        // 将按钮添加到“Setting”对话框
        // 设置按钮的位置和大小
        confirm.setBounds(133, 573, 70, 30)
        add(confirm)

        // 将“Setting”对话框设置为空布局
        layout = null
        // 将“Setting”对话框设置为不可调整大小
        isResizable = false
        // 设置“Setting”对话框的标题
        title = "Setting"
        // 验证“Setting”对话框及其所有子组件
        validate()
        // 设置“Setting”对话框关闭时的操作
        defaultCloseOperation = DISPOSE_ON_CLOSE
        // 设置“Setting”对话框在屏幕中的默认显示位置和大小
        setBounds(client.screenHalfW - 171, client.screenHalfH - 324, 342, 648)
    }


    // 显示下拉列表当前地图编号的预览图
    override fun itemStateChanged(e: ItemEvent) {
        if (e.source == stageList) {
            previewStage.icon = ImageIcon(Animator.getImage("Stage/${stageList.selectedItem}"))
            validate()
        }
    }

    // 当玩家按下“OK”按钮时
    override fun actionPerformed(e: ActionEvent) {
        // 获取下拉列表当前选中的地图编号
        Config.stageIndex = stageList.selectedIndex + 1
        // 释放“Setting”对话框所占用的屏幕资源
        dispose()
    }
}