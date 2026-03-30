/**
 * @typedef {"PROFILE_PICTURE"|"ATTACHMENT"|"EMOTE"} MediaOrigin
 * @typedef {"DOWNLOADING"|"STORED"|"CORRUPT"|"DELETING"|"DELETED"} MediaDataStatus
 * @typedef {"PICTURE"|"SVG"|"VIDEO"|
 *           "AUDIO"|"PDF"|"TEXT"|
 *           "OFFICE"|"ARCHIVE"|"CODE"|
 *           "FONT"|"MODEL"|"EXECUTABLE"|
 *           "DATA"|"OTHER"} FileType
 */

class MediaDataRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  name
  /** @type {string} */
  url
  /** @type {MediaOrigin} */
  origin
  /** @type {MediaDataStatus} */
  status
  /** @type {FileType} */
  type
}

class EmoteRepresentation {
  /** @type {string} */
  id
  /** @type {string} */
  name
  /** @type {string[]} */
  keywords
}

class MediaSettings {
  /** @type {number} */
  maxFileSize
}