import {describe, expect, test} from "vitest";
import {isUUID} from "./string.utils.js";

describe('isUUID', () => {
  const uuids = [
    {value: '114d8209-897f-410a-8562-8a2257d26d96', result: true},
    {value: 'cf7f4279-5472-4b71-825b-db37957f8428', result: true},
    {value: '8fe55dcb-8708-4b07-8e67-3ef2d157e344', result: true},
    {value: '8fe55dcb-8708-4b07-3ef2d157e344', result: false},
    {value: 'this-is-not-a-uuid', result: false},
    {value: '8fe55dcb87084b078e673ef2d157e344', result: false},
    {value: '', result: false},
    {value: null, result: false},
  ]

  for (const uuid of uuids) {
    test(`${uuid.value} => ${uuid.result}`, () => {
      expect(isUUID(uuid.value)).toBe(uuid.result);
    });
  }
});