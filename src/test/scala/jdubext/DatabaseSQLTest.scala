package com.github.ornicar.jdubext

import com.codahale.jdub._
import org.specs2.mutable._

class DatabaseSQLTest extends Specification {

  "SQL values" should {
    "process empty data" in {
      SQL.data() mustEqual SQL.Data(
        List(),
        List(),
        List()
      )
    }
    "process simple data" in {
      SQL.data(
        "name" -> "?" -> "John",
        "number" -> "?" -> 33
      ) mustEqual SQL.Data(
        List("name", "number"),
        List("?", "?"),
        List("John", 33)
      )
    }
    "process typed data" in {
      SQL.data(
        "name" -> "?::uuid" -> "John",
        "ip" -> "?::inet" -> "127.0.0.1"
      ) mustEqual SQL.Data(
        List("name", "ip"),
        List("?::uuid", "?::inet"),
        List("John", "127.0.0.1")
      )
    }
    "process simple and typed data" in {
      SQL.data(
        "name" -> "?" -> "John",
        "ip" -> "?::inet" -> "127.0.0.1"
      ) mustEqual SQL.Data(
        List("name", "ip"),
        List("?", "?::inet"),
        List("John", "127.0.0.1")
      )
    }
    "remove null values" in {
      SQL.data(
        "number" -> "?" -> None,
        "name" -> "?" -> "John",
        "foo" -> "?" -> null
      ) mustEqual SQL.Data(
        List("name"),
        List("?"),
        List("John")
      )
    }
    "deal with embedded queries" in {
      val call = SQL.Embed("do_something(?, ?)", "foo" :: "bar" :: Nil)
      SQL.data(
        "name" -> "?" -> "John",
        "query" -> "?" -> call,
        "color" -> "?" -> "black"
      ) mustEqual SQL.Data(
        List("name", "query", "color"),
        List("?", "do_something(?, ?)", "?"),
        List("John", "foo", "bar", "black")
      )
    }
  }
}