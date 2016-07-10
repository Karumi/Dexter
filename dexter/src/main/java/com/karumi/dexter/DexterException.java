package com.karumi.dexter;

import com.karumi.dexter.listener.DexterError;

final class DexterException extends IllegalStateException {

  public final DexterError error;

  public DexterException(String detailMessage, DexterError error) {
    super(detailMessage);
    this.error = error;
  }
}
