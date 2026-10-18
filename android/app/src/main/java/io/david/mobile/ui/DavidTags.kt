package io.david.mobile.ui

/*
 * David's own tags (DAVID.IO, 2026-10-07): stickers after NTL's 164 in the tag
 * table (tools/david-tag-append.py). Their `ntlId` is DAVID_TAG_BASE + their
 * number, which is never an NTL number, and the number itself travels in the
 * skin block's corner. On the sheet each one is turned 90 degrees
 * anticlockwise so it hangs off the rope like a pendant; a picker turns it
 * back upright.
 */
const val DAVID_TAG_BASE = 100000

fun isDavidTag(ntlId: Int): Boolean = ntlId >= DAVID_TAG_BASE

/** Whether the tag at this sheet index is one of David's own. */
fun isDavidTagIndex(index: Int): Boolean = SkinCatalog.tags.getOrNull(index)?.let { isDavidTag(it.ntlId) } == true

/** Sheet indices in picker order: David's own first, then NTL's. */
val TagPickerOrder: List<Int> by lazy {
    SkinCatalog.tags.filter { isDavidTag(it.ntlId) }.map { it.id } +
        SkinCatalog.tags.filter { !isDavidTag(it.ntlId) }.map { it.id }
}
