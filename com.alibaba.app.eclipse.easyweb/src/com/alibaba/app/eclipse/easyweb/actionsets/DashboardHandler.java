package com.alibaba.app.eclipse.easyweb.actionsets;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.editors.AppBowserEditor;
import com.alibaba.app.eclipse.easyweb.editors.AppEditorInput;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class DashboardHandler extends AbstractHandler {

    public AppEditorInput input = new AppEditorInput("Dashboard");

    /**
     * The constructor.
     */
    public DashboardHandler(){
    }

    /**
     * the command has been executed, so extract extract the needed information from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IWorkbenchPage page = window.getActivePage();
        Object[] obj = page.getEditors();
        IEditorPart editorp = page.getActiveEditor();
        IEditorPart editor = page.findEditor(input);
        if (null != editor) {
            page.bringToTop(editor);
        } else {
            try {
                input = new AppEditorInput("Dashboard");
                page.openEditor(input, AppBowserEditor.ID);
            } catch (PartInitException e) {
                EasywebPlugin.log(e);
            }
        }

        return null;
    }
}
