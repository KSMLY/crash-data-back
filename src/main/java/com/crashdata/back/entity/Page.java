package com.crashdata.back.entity;

import java.util.List;

/** One page of a search result plus how many rows matched in total. */
public record Page<T>(List<T> content, long totalElements) {
}
