package com.github.ornicar.jdubext

import com.codahale.jdub.Row
import org.joda.time.DateTime

object RowConversions extends RowConversions

trait RowConversions {

  implicit def richRow(row: Row) = new {

    /**
     * Extract the value at the given offset as an Option[DateTime].
     */
    def jodaDateTime(index: Int) = new DateTime(row time index)

    /**
     * Extract the value with the given name as an Option[DateTime].
     */
    def jodaDateTime(name: String) = new DateTime(row time name)
  }
}
