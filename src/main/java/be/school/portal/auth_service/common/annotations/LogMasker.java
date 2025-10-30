package be.school.portal.auth_service.common.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class LogMasker {

  private LogMasker() {}

  public static String mask(Object obj) {
    if (obj == null) return "null";

    Class<?> clazz = obj.getClass();

    // Primitive, wrapper, String
    if (isPrimitiveOrWrapper(clazz) || obj instanceof String) return String.valueOf(obj);

    // Skip core JDK types & common collections/maps
    if (clazz.getPackageName().startsWith("java.")
        || obj instanceof Collection
        || obj instanceof Map) {
      return obj.toString();
    }

    // Handle records
    if (clazz.isRecord()) {
      return maskRecord(obj, clazz);
    }

    // Fallback: POJO
    return maskPojo(obj, clazz);
  }

  private static String maskRecord(Object record, Class<?> clazz) {
    StringBuilder sb = new StringBuilder(clazz.getSimpleName()).append("[");
    RecordComponent[] components = clazz.getRecordComponents();
    for (int i = 0; i < components.length; i++) {
      RecordComponent rc = components[i];
      Hide hide = rc.getAnnotation(Hide.class);
      Object value = invokeRecordGetter(record, rc);

      sb.append(rc.getName()).append("=");

      if (hide != null) {
        sb.append(applyMask(value, hide.value()));
      } else {
        sb.append(mask(value));
      }

      if (i < components.length - 1) sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static Object invokeRecordGetter(Object record, RecordComponent rc) {
    try {
      return rc.getAccessor().invoke(record);
    } catch (ReflectiveOperationException e) {
      return "???";
    }
  }

  private static String maskPojo(Object obj, Class<?> clazz) {
    StringBuilder sb = new StringBuilder(clazz.getSimpleName()).append("{");
    Field[] fields = clazz.getDeclaredFields();

    for (int i = 0; i < fields.length; i++) {
      Field f = fields[i];
      Hide hide = f.getAnnotation(Hide.class);

      sb.append(f.getName()).append("=");

      try {
        if (!f.canAccess(obj)) f.setAccessible(true); // safer access
        Object value = f.get(obj);
        if (hide != null) sb.append(applyMask(value, hide.value()));
        else sb.append(mask(value));
      } catch (Throwable e) {
        sb.append("???"); // ignore inaccessible fields
      }

      if (i < fields.length - 1) sb.append(", ");
    }

    sb.append("}");
    return sb.toString();
  }

  private static String applyMask(Object value, Hide.MaskStrategy strategy) {
    return switch (strategy) {
      case OMIT -> "";
      case HASH -> String.valueOf(Objects.hashCode(value));
      case MASK -> "***masked***";
    };
  }

  private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
    return clazz.isPrimitive()
        || clazz.equals(Boolean.class)
        || clazz.equals(Byte.class)
        || clazz.equals(Character.class)
        || clazz.equals(Double.class)
        || clazz.equals(Float.class)
        || clazz.equals(Integer.class)
        || clazz.equals(Long.class)
        || clazz.equals(Short.class);
  }
}
