/*
 * Copyright 2018 Tzu-Chiao Yeh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kuery.stock

sealed trait Stock {
  val id = Transaction.generate

  override def toString: String = s"[$id] type: ${this.getClass.getCanonicalName}, "
}

case class Clothes(val name: String, val vendor: String, val size: ClothesSize) extends Stock {

  override def toString: String = super.toString + s"name: $name, vendor: $vendor, size: $size"
}

case class Shoes(val name: String, val vendor: String, val size: Int) extends Stock {
  // assertion for valid shoe size.
  require((size >= 20) && (size <= 30))

  override def toString: String = super.toString + s"name: $name, vendor: $vendor, size: $size"
}

sealed trait ClothesSize
case object XL extends ClothesSize
case object L extends ClothesSize
case object M extends ClothesSize
case object S extends ClothesSize
case object XS extends ClothesSize
