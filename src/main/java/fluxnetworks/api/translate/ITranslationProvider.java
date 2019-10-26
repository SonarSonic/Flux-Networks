package fluxnetworks.api.translate;

import java.util.List;

public interface ITranslationProvider {

    List<Translation> getTranslations(List<Translation> translations);
}
