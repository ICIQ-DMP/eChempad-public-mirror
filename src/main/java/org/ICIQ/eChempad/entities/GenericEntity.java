package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// To deserialize generics
@JsonTypeInfo(
        use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.EXISTING_PROPERTY,
        property="typeName")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Journal.class, name = "Journal"),
        @JsonSubTypes.Type(value = Authority.class, name = "Authority"),
        @JsonSubTypes.Type(value = Researcher.class, name = "Researcher")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  //https://stackoverflow.com/questions/67353793/what-does-jsonignorepropertieshibernatelazyinitializer-handler-do
public abstract class GenericEntity implements IEntity{

    public GenericEntity() {}

    @JsonIgnore
    public abstract String getTypeName();
}
