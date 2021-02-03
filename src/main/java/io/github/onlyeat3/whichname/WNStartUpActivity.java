package io.github.onlyeat3.whichname;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class WNStartUpActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        TranslationService translationService = ServiceManager.getService(TranslationService.class);
        translationService.loadAll();
    }
}
