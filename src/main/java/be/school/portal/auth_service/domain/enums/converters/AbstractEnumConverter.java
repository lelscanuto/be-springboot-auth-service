package be.school.portal.auth_service.domain.enums.converters;

import jakarta.persistence.AttributeConverter;

public abstract class AbstractEnumConverter<T extends Enum<T> & ConvertibleEnum<E>, E>
        implements AttributeConverter<T, E> {
    private final Class<T> clazz;

    protected AbstractEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        if (dbData == null) {
            return null;
        }

        T[] enums = clazz.getEnumConstants();

        for (T e : enums) {
            if (e.getValue().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException();
    }
}