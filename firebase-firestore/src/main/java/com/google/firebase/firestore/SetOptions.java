// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.firestore;

import static com.google.common.base.Preconditions.checkArgument;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.annotations.PublicApi;
import com.google.firebase.firestore.model.mutation.FieldMask;
import java.util.ArrayList;
import java.util.List;

/**
 * An options object that configures the behavior of set() calls. By providing one of the SetOptions
 * objects returned by {@link #merge}, {@link #mergeFields} and {@link #mergeFieldPaths}, the set()
 * calls in {@link DocumentReference}, {@link WriteBatch} and {@link Transaction} can be configured
 * to perform granular merges instead of overwriting the target documents in their entirety.
 */
@PublicApi
public final class SetOptions {

  static final SetOptions OVERWRITE = new SetOptions(false, null);
  private static final SetOptions MERGE_ALL_FIELDS = new SetOptions(true, null);

  private final boolean merge;
  @Nullable private final FieldMask fieldMask;

  private SetOptions(boolean merge, @Nullable FieldMask fieldMask) {
    checkArgument(fieldMask == null || merge, "Cannot specify a fieldMask for non-merge sets()");
    this.merge = merge;
    this.fieldMask = fieldMask;
  }

  /** @hide */
  public boolean isMerge() {
    return merge;
  }

  /** @hide */
  @Nullable
  public FieldMask getFieldMask() {
    return fieldMask;
  }

  /**
   * Changes the behavior of set() calls to only replace the values specified in its data argument.
   * Fields omitted from the set() call will remain untouched.
   */
  @NonNull
  @PublicApi
  public static SetOptions merge() {
    return MERGE_ALL_FIELDS;
  }

  /**
   * Changes the behavior of set() calls to only replace the fields under fieldPaths. Any field that
   * is not specified in fieldPaths is ignored and remains untouched.
   *
   * <p>It is an error to pass a SetOptions object to a set() call that is missing a value for any
   * of the fields specified here.
   *
   * @param fields The list of fields to merge. Fields can contain dots to reference nested fields
   *     within the document.
   */
  @NonNull
  @PublicApi
  public static SetOptions mergeFields(List<String> fields) {
    List<com.google.firebase.firestore.model.FieldPath> fieldPaths = new ArrayList<>();

    for (String field : fields) {
      fieldPaths.add(FieldPath.fromDotSeparatedPath(field).getInternalPath());
    }

    return new SetOptions(true, FieldMask.fromCollection(fieldPaths));
  }

  /**
   * Changes the behavior of set() calls to only replace the fields under fieldPaths. Any field that
   * is not specified in fieldPaths is ignored and remains untouched.
   *
   * <p>It is an error to pass a SetOptions object to a set() call that is missing a value for any
   * of the fields specified here.
   *
   * @param fields The list of fields to merge. Fields can contain dots to reference nested fields
   *     within the document.
   */
  @NonNull
  @PublicApi
  public static SetOptions mergeFields(String... fields) {
    List<com.google.firebase.firestore.model.FieldPath> fieldPaths = new ArrayList<>();

    for (String field : fields) {
      fieldPaths.add(FieldPath.fromDotSeparatedPath(field).getInternalPath());
    }

    return new SetOptions(true, FieldMask.fromCollection(fieldPaths));
  }

  /**
   * Changes the behavior of set() calls to only replace the fields under fieldPaths. Any field that
   * is not specified in fieldPaths is ignored and remains untouched.
   *
   * <p>It is an error to pass a SetOptions object to a set() call that is missing a value for any
   * of the fields specified here in its to data argument.
   *
   * @param fields The list of fields to merge.
   */
  @NonNull
  @PublicApi
  public static SetOptions mergeFieldPaths(List<FieldPath> fields) {
    List<com.google.firebase.firestore.model.FieldPath> fieldPaths = new ArrayList<>();

    for (FieldPath field : fields) {
      fieldPaths.add(field.getInternalPath());
    }

    return new SetOptions(true, FieldMask.fromCollection(fieldPaths));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SetOptions that = (SetOptions) o;

    if (merge != that.merge) {
      return false;
    }
    return fieldMask != null ? fieldMask.equals(that.fieldMask) : that.fieldMask == null;
  }

  @Override
  public int hashCode() {
    int result = (merge ? 1 : 0);
    result = 31 * result + (fieldMask != null ? fieldMask.hashCode() : 0);
    return result;
  }
}
