import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;

public class CreatePatcherAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile[] data = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        // TODO: insert action logic here
        PatcherDialog dialog = new PatcherDialog(e);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();

        PsiFile psi = e.getData(DataKeys.PSI_FILE);
        FileViewProvider viewProvider = psi.getViewProvider();
        for (Language l : viewProvider.getLanguages()) {
            System.out.println(l);
        }
    }
}
