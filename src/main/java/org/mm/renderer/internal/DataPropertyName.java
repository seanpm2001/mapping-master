package org.mm.renderer.internal;

import javax.annotation.Nonnull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 *         Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyName extends PropertyName {

   public DataPropertyName(@Nonnull String prefixedName) {
      super(prefixedName);
   }

   @Override
   public DataPropertyName update(String newPrefixedName) {
      return new DataPropertyName(newPrefixedName);
   }

   @Override
   public boolean isDataProperty() {
      return true;
   }

   @Override
   public boolean isObjectProperty() {
      return false;
   }

   @Override
   public boolean isAnnotationProperty() {
      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      }
      if (this == o) {
         return true;
      }
      if (!(o instanceof DataPropertyName)) {
         return false;
      }
      DataPropertyName other = (DataPropertyName) o;
      return Objects.equal(this.getString(), other.getString());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.getString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
            .addValue(this.getString())
            .toString();
   }
}
