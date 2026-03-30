export type SanctionType = "VOICE_TIME_OUT" | "TEXT_TIME_OUT" | "BAN";

export class SanctionUserRepresentation {
    id: string;
    displayName: string;
}

export class SanctionRepresentation {
    id: string;
    targetedUser: SanctionUserRepresentation;
    server: string;
    type: SanctionType;
    startAt: string;
    expiresAt: string;
    issuedBy: SanctionUserRepresentation;
    reason: string;
    revokedBy: SanctionUserRepresentation;
    revokedAt: string;
    active: boolean;
}

export class SanctionRevocationRequestRepresentation {
    id: string;
    sanctionId: string;
    message: string;
    status: string;
    requestAt: string;
    canRequestAgain: boolean;
}