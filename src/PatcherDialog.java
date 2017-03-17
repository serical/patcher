import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.*;

public class PatcherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField textField;
    private JButton fileChooseBtn;
    private JPanel filePanel;
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
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fileChooser.showOpenDialog(null);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    System.out.println(fileChooser.getSelectedFile().getPath());
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
        ListModel model = fieldList.getModel();
        if (model.getSize() == 0) {
            Messages.showErrorDialog(this, "Please Select Export File!", "Error");
            return;
        }
        for (int i = 0; i < model.getSize(); i++) {
            Object element = model.getElementAt(i);
            System.out.println(element);
        }

        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        Object[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        fieldList = new JBList(data);
        fieldList.setEmptyText("No File Selected!");
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        filePanel = decorator.createPanel();
    }
}
