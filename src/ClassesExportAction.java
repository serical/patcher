import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Created by serical on 2017/3/17.
 */
public class ClassesExportAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            com.intellij.openapi.actionSystem.DataContext dataContext = e.getDataContext();
            PsiJavaFile javaFile = (PsiJavaFile) ((PsiFile) DataKeys.PSI_FILE.getData(dataContext)).getContainingFile();
            String sourceName = javaFile.getName();
            Module module = (Module) DataKeys.MODULE.getData(dataContext);
            String compileRoot = CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getPath();
            getVirtualFile(sourceName, CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getChildren(), compileRoot);
            VirtualFileManager.getInstance().syncRefresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.showErrorDialog("Please build your module or project!!!", "error");
        }
    }

    private void getVirtualFile(String sourceName, VirtualFile virtualFile[], String compileRoot)
            throws Exception {
        if (!ArrayUtils.isEmpty(virtualFile)) {
            VirtualFile arr$[] = virtualFile;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                VirtualFile vf = arr$[i$];
                String srcName;
                if (StringUtils.indexOf(vf.toString(), "$") != -1)
                    srcName = StringUtils.substring(vf.toString(), StringUtils.lastIndexOf(vf.toString(), "/") + 1, StringUtils.indexOf(vf.toString(), "$"));
                else
                    srcName = StringUtils.substring(vf.toString(), StringUtils.lastIndexOf(vf.toString(), "/") + 1, StringUtils.length(vf.toString()) - 6);
                String dstName = StringUtils.substring(sourceName, 0, StringUtils.length(sourceName) - 5);
                if (StringUtils.equals(srcName, dstName)) {
                    String outRoot = (new StringBuilder()).append(StringUtils.substring(compileRoot, 0, StringUtils.lastIndexOf(compileRoot, "/"))).append("/out").toString();
                    String packagePath = StringUtils.substring(vf.getPath(), StringUtils.length(compileRoot), StringUtils.length(vf.getPath()));
                    File s = new File(vf.getPath());
                    File t = new File((new StringBuilder()).append(outRoot).append(packagePath).toString());
                    FileUtil.copy(s, t);
                }
                if (!ArrayUtils.isEmpty(virtualFile))
                    getVirtualFile(sourceName, vf.getChildren(), compileRoot);
            }

        }
    }
}
