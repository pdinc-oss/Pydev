package org.python.pydev.customizations.app_engine.launching;

import java.util.Map;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.python.pydev.core.log.Log;
import org.python.pydev.navigator.elements.IWrappedResource;
import org.python.pydev.plugin.nature.PythonNature;

/**
 * Test to check if a given container can be run from google app engine.
 * 
 * @author Fabio
 */
public class AppEnginePropertyTester extends PropertyTester{


    /**
     * Expected value is ignored.
     * 
     * Considers as available for the run a container of a project with the GOOGLE_APP_ENGINE variable
     * declared in it and has a app.yaml or app.yml under it.
     */
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        IContainer container = null;
        if(receiver instanceof IWrappedResource){
            IWrappedResource wrappedResource = (IWrappedResource) receiver;
            Object actualObject = wrappedResource.getActualObject();
            if(actualObject instanceof IContainer){
                container = (IContainer) actualObject;
            }
        }
        if(receiver instanceof IContainer){
            container = (IContainer) receiver;
        }
        
        if(container == null){
            return false;
        }
        
        IProject project = container.getProject();
        if(project == null){
            return false;
        }
        
        PythonNature nature = PythonNature.getPythonNature(project);
        if(nature == null){
            return false;
        }
        
        //dev_appserver.py [options] <application root>
        //
        //Application root must be the path to the application to run in this server.
        //Must contain a valid app.yaml or app.yml file.
        IFile file = container.getFile(new Path("app.yaml"));
        if(file == null || !file.exists()){
            file = container.getFile(new Path("app.yml"));
            if(file == null || !file.exists()){
                return false;
            }
        }
        
        try{
            Map<String, String> variableSubstitution = nature.getPythonPathNature().getVariableSubstitution();
            //Only consider a google app engine a project that has a google app engine variable!
            if(variableSubstitution != null && variableSubstitution.containsKey(AppEngineConstants.GOOGLE_APP_ENGINE_VARIABLE)){
                return true;
            }
        }catch(Exception e){
            Log.log(e);
        }
     
        return false;
    }

}