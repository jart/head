/* This is for I18n in this order */
/* Iceland is inserted into country table */
INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(7,'Iceland','IS');

/*language*/
INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(190,74,' ');

/* Icelandic is inserted into the language table */
INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(2,'Icelandic','IS',190);


/* and the Iceland locale is inserted */
INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(2,7,2,'IS',0);

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 155 WHERE DATABASE_VERSION = 154;
