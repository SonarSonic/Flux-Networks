package sonar.fluxnetworks.common.handler;

import sonar.fluxnetworks.api.translate.ITranslationProvider;
import sonar.fluxnetworks.api.translate.Translation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LocalizationHandler implements ISelectiveResourceReloadListener {

    public List<ITranslationProvider> providers = new ArrayList<>();

    public void clear() {
        providers.clear();
    }

    public void add(ITranslationProvider handler) {
        providers.add(handler);
        loadHandler(handler);
    }

    public void remove(ITranslationProvider handler) {
        providers.remove(handler);
    }

    public void loadHandler(ITranslationProvider handler) {
        handler.getTranslations(new ArrayList<>()).forEach(LocalizationHandler::translate);
    }

    public static Translation translate(Translation l) {
        l.translated = translate(l.key);
        return l;
    }

    public static String translate(String string) {
        return new TextComponentTranslation(string).getFormattedText();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        providers.forEach(this::loadHandler);
    }
}
