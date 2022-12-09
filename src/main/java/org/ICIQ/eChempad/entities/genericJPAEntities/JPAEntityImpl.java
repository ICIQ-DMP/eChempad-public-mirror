/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.ICIQ.eChempad.entities.DocumentWrapper;

/**
 * This class is the generic type that can be seen as {@code T} in the repositories, services and controllers of this
 * application. This abstract class is used in order to be able to use certain annotations that would not work directly
 * in an interface, in our case {@code JPAEntity}.
 *
 * A relevant annotation regarding this matter is the annotation {@code @JsonInclude(JsonInclude.Include.NON_NULL)},
 * which makes Jackson ignore null fields when deserializing into a memory entity. This means that if some fields that
 * are expected in JSON for a certain entity implementing this class are not present in the JSON, Jackson will ignore
 * the values and not set them into the parsed entity. The default behaviour for a "data" field is set them to null if
 * they are not present in the JSON.
 *
 * There is also {@code JsonTypeInfo}, which is used to create a field called "typeName" that contains the actual type
 * of the instance that should be parsed from the JSON and {@code JsonSubTypes}, which is used to declare which values
 * are allowed into this "typeName" field and what is the correspondence to entity classes.
  */
@JsonTypeInfo(
        use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.EXISTING_PROPERTY,
        property="typeName")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Journal.class, name = "Journal"),
        @JsonSubTypes.Type(value = Authority.class, name = "Authority"),
        @JsonSubTypes.Type(value = Researcher.class, name = "Researcher"),
        @JsonSubTypes.Type(value = Experiment.class, name = "Experiment"),
        @JsonSubTypes.Type(value = DocumentWrapper.class, name = "Document")
})
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JPAEntityImpl implements JPAEntity {

    public JPAEntityImpl() {}

}
