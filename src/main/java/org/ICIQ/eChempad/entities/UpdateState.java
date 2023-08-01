package org.ICIQ.eChempad.entities;

/**
 * Describes the state of the data present in eChempad in relation with the corresponding data present in the original
 * platform, such as Perkin-Elmer Signals Notebook.
 *
 * - NOT_PRESENT: We can just save the entity, it is the first time that this entity has been read. We can just save the
 * read entity into eChempad without any special behaviour.
 * - UP_TO_DATE: The entity read is already present in the database of eChempad and their content is the same. This
 * probably means that we need to do nothing.
 * - ECHEMPAD_HAS_CHANGES: There are changes in eChempad that are not present in the corresponding entity in the
 * original platform. This means that we should show a warning or error saying that if we import this DataEntity, we may
 * lose data that is in eChempad, since the entity data will overwrite the data that has modifications in eChempad.
 * - ORIGIN_HAS_CHANGES: There are changes in the entity of the origin platform that are not present in eChempad. This
 * means that we need to run the update algorithm. This algorithm only reads and updates with the received data the
 * descending entities or the changing properties of the entity in eChempad.
 * - BOTH_HAVE_CHANGES: Irreconcilable changes. Both platforms have changes from the original material. This means that
 * data loss is ensured, since not losing data would mean doing a git merge of the conflicting data. We can choose
 * between doing nothing, which would mean that we discard the data present in the origin platform and the good version
 * is the one present in eChempad, or we can perform an updating algorithm, which will mean that the good version is in
 * the original platform, and we are overwriting the entity present in eChempad with the data from the original
 * platform, losing the changes that we have made in eChempad.
 */
public enum UpdateState {
    NOT_PRESENT,
    UP_TO_DATE,
    ECHEMPAD_HAS_CHANGES,
    ORIGIN_HAS_CHANGES,
    BOTH_HAVE_CHANGES
}
