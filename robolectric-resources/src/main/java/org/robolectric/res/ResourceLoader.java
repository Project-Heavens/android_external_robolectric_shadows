package org.robolectric.res;

import org.jetbrains.annotations.NotNull;
import org.robolectric.res.builder.XmlBlock;

import java.io.InputStream;

public abstract class ResourceLoader {

  public abstract TypedResource getValue(@NotNull ResName resName, String qualifiers);

  public TypedResource getValue(int resId, String qualifiers) {
    ResName resName = getResourceIndex().getResName(resId);
    return resName != null ? getValue(resName, qualifiers) : null;
  }

  public Plural getPlural(int resId, int quantity, String qualifiers) {
    PluralResourceLoader.PluralRules pluralRules = (PluralResourceLoader.PluralRules) getValue(resId, qualifiers);
    if (pluralRules == null) return null;

    return pluralRules.find(quantity);
  }

  public abstract XmlBlock getXml(ResName resName, String qualifiers);

  public XmlBlock getXml(int resId, String qualifiers) {
    ResName resName = resolveResName(resId, qualifiers);
    return resName != null ? getXml(resName, qualifiers) : null;
  }

  public abstract InputStream getRawValue(ResName resName);

  public InputStream getRawValue(int resId) {
    return getRawValue(getResourceIndex().getResName(resId));
  }

  public abstract ResourceIndex getResourceIndex();

  public abstract boolean providesFor(String namespace);

  private ResName resolveResName(int resId, String qualifiers) {
    TypedResource value = getValue(resId, qualifiers);
    return resolveResource(value, qualifiers, resId);
  }

  private ResName resolveResource(TypedResource value, String qualifiers, int resId) {
    ResName resName = getResourceIndex().getResName(resId);
    while (value != null && value.isReference()) {
      String s = value.asString();
      if (AttributeResource.isNull(s) || AttributeResource.isEmpty(s)) {
        value = null;
      } else {
        String refStr = s.substring(1).replace("+", "");
        resName = ResName.qualifyResName(refStr, resName);
        value = getValue(resName, qualifiers);
      }
    }

    return resName;
  }

  public TypedResource resolveResourceValue(TypedResource value, String qualifiers, int resId) {
    ResName resName = getResourceIndex().getResName(resId);
    while (value != null && value.isReference()) {
      String s = value.asString();
      if (AttributeResource.isNull(s) || AttributeResource.isEmpty(s)) {
        value = null;
      } else {
        String refStr = s.substring(1).replace("+", "");
        resName = ResName.qualifyResName(refStr, resName);
        value = getValue(resName, qualifiers);
      }
    }

    return value;
  }
}
