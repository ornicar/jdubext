package com.github.ornicar.jdubext

import com.codahale.jdub._
import scalaz.{ NonEmptyList, NonEmptyLists }

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

    (Data(Nil, Nil, Nil) /: pairs.reverse) { (acc, pair) ⇒
      val ((name, tpe), value) = pair
      val valueOrNull = value match {
        case None | Some(None) | Some("") ⇒ null
        case Some(x)                      ⇒ x
        case x                            ⇒ x
      }
      valueOrNull match {
        case null     ⇒ acc
        case v: Embed ⇒ acc.aggregate(name, v.sql, v.values)
        case v        ⇒ acc.aggregate(name, tpe, List(v))
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

    def setters = nameSeq zip typeSeq map (a ⇒ a._1 + " = " + a._2) mkString ", "

    def insertIn(table: String) = SQL(
      "INSERT INTO %s (%s) VALUES (%s)".format(table, names, types),
      values)

    def updateIn(table: String) = new {
      def where(condition: (String, Seq[Any])) = SQL(
        "UPDATE %s SET %s WHERE %s".format(table, setters, condition._1),
        values ::: condition._2.toList)
    }

    def +(other: Data): Multi = Multi(this, other)
  }

  case class Multi(datas: NonEmptyList[Data]) {

    def +(other: Data): Multi = Multi(other, datas.list)
    def +(other: Multi): Multi = Multi(other.datas.head, other.datas.tail ::: datas.list)

    def insertIn(table: String) = SQL(
      "INSERT INTO %s (%s) VALUES (%s)".format(
        table,
        datas.head.names,
        datas.list map (_.types) mkString "), ("),
      datas.list map (_.values) flatten
    )
  }

  object Multi extends NonEmptyLists {

    def apply(data: Data, others: Data*): Multi = apply(data, others.toList)

    def apply(data: Data, others: List[Data]): Multi = new Multi(nel(data, others))

    def apply(datas: List[Data]): Option[Multi] = datas.headOption map { head ⇒
      apply(head, datas.tail)
    }
  }
}
