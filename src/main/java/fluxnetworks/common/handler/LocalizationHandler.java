package fluxnetworks.common.handler;

import fluxnetworks.common.core.ILocalizationProvider;
import fluxnetworks.common.core.Localization;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LocalizationHandler implements ISelectiveResourceReloadListener {

    public List<ILocalizationProvider> providers = new ArrayList<>();

    public void clear() {
        providers.clear();
    }

    public void add(ILocalizationProvider handler) {
        providers.add(handler);
        loadHandler(handler);
    }

    public void remove(ILocalizationProvider handler) {
        providers.remove(handler);
    }

    public void loadHandler(ILocalizationProvider handler) {
        handler.getLocalizations(new ArrayList<>()).forEach(LocalizationHandler::translate);
    }

    public static Localization translate(Localization l) {
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
