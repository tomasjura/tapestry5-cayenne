package com.googlecode.tapestry5cayenne.internal;

/**
 * Search types possible for EntityField auto-completion.
 * PREFIX: field like 'input%';
 * SUFFIX: field like '%input';
 * ANYWHERE: field like '%input%';
 * @author robertz
 *
 */
public enum SearchType {
PREFIX, SUFFIX, ANYWHERE;
}
