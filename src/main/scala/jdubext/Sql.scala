package com.github.ornicar.jdubext

import com.codahale.jdub._

case class SQL(sql: String, values: Seq[Any] = Nil) extends Statement

object SQL {

  case class Embed(sql: String, values: List[Any])

  /**
   * Converts things like
   * "in_site_id"       -> "?" -> siteId,
   * "in_visitor_hash"  -> "?::uuid" -> visitorHash,
   * to Data
   * It also removes null mappings
   */
  def data(pairs: ((String, String), Any)*): Data = {

    (Data(Nil, Nil, Nil) /: pairs.reverse) { (acc, pair) =>
      val ((name, tpe), value) = pair
      val valueOrNull = value match {
        case None | Some(None) | Some("") => null
        case Some(x) => x
        case x => x
      }
      valueOrNull match {
        case null => acc
        case v: Embed => acc.aggregate(name, v.sql, v.values)
        case v => acc.aggregate(name, tpe, List(v))
      }
    }
  }

  case class Data(nameSeq: List[String], typeSeq: List[String], valueSeq: List[Any]) {
    def aggregate(name: String, tpe: String, vals: List[Any]) = Data(
      name :: nameSeq,
      tpe :: typeSeq,
      vals ::: valueSeq
    )

    def names = nameSeq mkString ", "

    def types = typeSeq mkString ", "

    def values = valueSeq

    def toSql(pattern: String) = SQL(pattern.format(names, types), values)
  }
}
