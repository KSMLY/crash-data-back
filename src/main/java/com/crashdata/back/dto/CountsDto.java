package com.crashdata.back.dto;

/** A KPI value for the current window and the window before it. */
public record CountsDto(long current, long previous) {
}
