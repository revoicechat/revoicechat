/**
 * @template T
 */
class PageResult {
  /** @type {T[]} */
  content
  /** @type {number} */
  pageNumber
  /** @type {number} */
  pageSize
  /** @type {number} */
  totalElements
  /** @type {number} */
  totalPages
}