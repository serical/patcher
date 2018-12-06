import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class PatcherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField textField;
    private JButton fileChooseBtn;
    private JPanel filePanel;
    private JTextField webTextField;
    private AnActionEvent event;
    private JBList fieldList;

    PatcherDialog(final AnActionEvent event) {
        this.event = event;
        setTitle("Create Patcher Dialog");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // 保存路径按钮事件
        fileChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userDir = System.getProperty("user.home");
                JFileChooser fileChooser = new JFileChooser(userDir + "/Desktop");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fileChooser.showOpenDialog(null);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

    }

    private void onOK() {
        // 条件校验
        if (null == textField.getText() || "".equals(textField.getText())) {
            Messages.showErrorDialog(this, "Please Select Save Path!", "Error");
            return;
        }

        ListModel<VirtualFile> model = fieldList.getModel();
        if (model.getSize() == 0) {
            Messages.showErrorDialog(this, "Please Select Export File!", "Error");
            return;
        }

        try {
            // 模块对象
            Module module = event.getData(DataKeys.MODULE);
            CompilerModuleExtension instance = CompilerModuleExtension.getInstance(module);
            // 编译目录
            String compilerOutputUrl = instance.getCompilerOutputPath().getPath();
            // JavaWeb项目的WebRoot目录
            String webPath = "/" + webTextField.getText() + "/";
            //项目模块的路径E:\git\moudlea\
            String modulePath = module.getModuleFilePath().split(".idea/")[0];
            // 导出目录
            String exportPath = textField.getText() + webPath;
            for (int i = 0; i < model.getSize(); i++) {
                VirtualFile element = model.getElementAt(i);
                String elementName = element.getName();
                String elementPath = element.getPath();
                if(elementPath.contains("/src/")){

                    //获取到src下的文件名或者目录名，如果是java文件则文件名由.java替换为.class并到编译路径下取，如果是文件夹则直接去编译路径下的文件夹
                    String className = File.separator + elementPath.split("/src/")[1].replace(".java", ".class");
                    File from = new File(compilerOutputUrl + className);
                    File to = new File(exportPath + "WEB-INF" + File.separator + "classes" + className);
                    FileUtil.copyFileOrDir(from, to);
                }else {
                    File from = new File(elementPath);
                    File to = null;
                    if(elementPath.contains(webPath)){
                        to = new File(exportPath + elementPath.split(webPath)[1]);
                    }else{
                        to = new File(textField.getText()+ File.separator+ elementPath.substring(modulePath.length()));
                    }
                    FileUtil.copyFileOrDir(from, to);
                }
            }
        } catch (Exception e) {
            Messages.showErrorDialog(this, "Create Patcher Error!"+e.getMessage(), "Error");
            e.printStackTrace();
        }

        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        fieldList = new JBList(data);
        fieldList.setEmptyText("No File Selected!");
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        filePanel = decorator.createPanel();
    }
}
