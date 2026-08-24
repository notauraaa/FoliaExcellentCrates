package su.nightexpress.excellentcrates.crate.cost.type.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.cost.CostEntry;
import su.nightexpress.excellentcrates.crate.cost.CostTypeId;
import su.nightexpress.excellentcrates.crate.cost.entry.impl.EcoCostEntry;
import su.nightexpress.excellentcrates.crate.cost.type.AbstractCostType;
import su.nightexpress.excellentcrates.dialog.DialogRegistry;
import com.notauraaa.folianightcore.config.ConfigValue;
import com.notauraaa.folianightcore.config.FileConfig;
import com.notauraaa.folianightcore.integration.currency.CurrencyId;
import com.notauraaa.folianightcore.integration.currency.EconomyBridge;
import com.notauraaa.folianightcore.locale.LangContainer;
import com.notauraaa.folianightcore.locale.LangEntry;
import com.notauraaa.folianightcore.locale.entry.IconLocale;
import com.notauraaa.folianightcore.locale.entry.TextLocale;
import com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static com.notauraaa.folianightcore.util.text.night.wrapper.TagWrappers.*;

public class EcoCostType extends AbstractCostType implements LangContainer {

    public static final TextLocale LOCALE_NAME = LangEntry.builder("Costs.Currency.Name").text(GREEN.wrap("[$]") + " " + WHITE.wrap("Currency"));

    public static final IconLocale LOCALE_EDIT_BUTTON = LangEntry.iconBuilder("Costs.Currency.EditButton")
        .rawName(YELLOW.and(BOLD).wrap("Currency Cost") + GRAY.wrap(" - ") + WHITE.wrap(GENERIC_NAME))
        .rawLore(ITALIC.and(DARK_GRAY).wrap("Press " + SOFT_RED.wrap(TagWrappers.KEY.apply("key.drop")) + " key to delete.")).br()
        .appendCurrent("Currency ID", GENERIC_ID)
        .appendCurrent("Amount", GENERIC_AMOUNT).br()
        .appendClick("Click to edit")
        .build();

    private final DialogRegistry dialogs;

    public EcoCostType(@NotNull CratesPlugin plugin, @NotNull DialogRegistry dialogs) {
        super(CostTypeId.CURRENCY);
        this.dialogs = dialogs;

        plugin.injectLang(this);
    }

    @Override
    public boolean isAvailable() {
        return EconomyBridge.hasCurrency();
    }

    @Override
    @NotNull
    public String getName() {
        return LOCALE_NAME.text();
    }

    @Override
    @NotNull
    public CostEntry load(@NotNull FileConfig config, @NotNull String path) {
        String currencyId = ConfigValue.create(path + ".Currency", CurrencyId.VAULT).read(config);
        double amount = ConfigValue.create(path + ".Amount", 0D).read(config);

        return new EcoCostEntry(this, this.dialogs, currencyId, amount);
    }

    @Override
    @NotNull
    public EcoCostEntry createEmpty() {
        return new EcoCostEntry(this, this.dialogs, CurrencyId.VAULT, 0);
    }
}
