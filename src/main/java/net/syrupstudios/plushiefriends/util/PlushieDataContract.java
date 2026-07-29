package net.syrupstudios.plushiefriends.util;

import net.minecraft.nbt.Tag;

/**
 * Stable names and precedence rules for persisted plushie data.
 *
 * <p>This class deliberately describes storage; it does not migrate data. Code which reads an
 * item must prefer canonical fields over legacy fields on a field-by-field basis. Consequently,
 * a canonical {@value #OWNER} and a legacy {@value #LORE} may legitimately be read from the same
 * item. Writers must only produce the canonical representation for their Minecraft version.</p>
 *
 * <h2>Block entities</h2>
 * <p>{@value #OWNER} and {@value #LORE} live directly in the block entity's root compound.</p>
 *
 * <h2>Items on Minecraft 1.20.1</h2>
 * <p>The canonical fields live directly in the item stack's root tag. The legacy representation
 * nests the same fields inside {@value #LEGACY_BLOCK_ENTITY_TAG}.</p>
 *
 * <h2>Items on Minecraft 1.21.1</h2>
 * <p>The canonical fields have the same logical shape, stored in the
 * {@value #ITEM_CUSTOM_DATA_COMPONENT} component. Legacy data may be found in the
 * {@value #LEGACY_ITEM_BLOCK_ENTITY_DATA_COMPONENT} component. Version-specific adapters are
 * responsible for accessing components; shared code should reason only about the logical fields
 * declared here.</p>
 */
public final class PlushieDataContract {
    /** An unresolved player name (string) or a frozen, resolved GameProfile (compound). */
    public static final String OWNER = "PlushieOwner";

    /** A list of string lore lines. */
    public static final String LORE = "PlushieLore";

    /** Legacy 1.20.1 item container. Never use this container for newly written items. */
    public static final String LEGACY_BLOCK_ENTITY_TAG = "BlockEntityTag";

    /** Canonical 1.21.1 item component containing {@link #OWNER} and {@link #LORE}. */
    public static final String ITEM_CUSTOM_DATA_COMPONENT = "minecraft:custom_data";

    /** Legacy 1.21.1 component corresponding to the old item BlockEntityTag payload. */
    public static final String LEGACY_ITEM_BLOCK_ENTITY_DATA_COMPONENT = "minecraft:block_entity_data";

    public static final byte OWNER_NAME_TAG_TYPE = Tag.TAG_STRING;
    public static final byte OWNER_PROFILE_TAG_TYPE = Tag.TAG_COMPOUND;
    public static final byte LORE_TAG_TYPE = Tag.TAG_LIST;
    public static final byte LORE_ELEMENT_TAG_TYPE = Tag.TAG_STRING;

    private PlushieDataContract() {
    }

    /**
     * Defines which value wins when canonical and legacy item data coexist.
     */
    public enum ItemFieldPrecedence {
        /** Read the canonical field when present; otherwise read that field from legacy data. */
        CANONICAL_THEN_LEGACY
    }

    public static final ItemFieldPrecedence ITEM_FIELD_PRECEDENCE =
            ItemFieldPrecedence.CANONICAL_THEN_LEGACY;
}
