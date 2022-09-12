package fr.inrae.metabolomics.p2m2.format.ms

case object Utils {
  def conversionEnumType[T<:Enumeration,V<:Enumeration](t : V, v : T#Value) : Option[V#Value] = {
    t.values.filter( _.toString == v.toString ).lastOption
  }
}
