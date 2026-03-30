/**
 * @typedef {"CREATED"|"USED"|"REVOKED"} InvitationLinkStatus
 * @typedef {"APPLICATION_JOIN"|"SERVER_JOIN"} InvitationType
 */

class InvitationRepresentation {
  /** @type {string} */
  id
  /** @type {InvitationLinkStatus} */
  status
  /** @type {InvitationType} */
  type
  /** @type {string} */
  targetedServer
}