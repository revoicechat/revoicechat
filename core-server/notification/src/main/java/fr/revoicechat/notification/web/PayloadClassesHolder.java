package fr.revoicechat.notification.web;

import java.util.Set;

import org.reflections.Reflections;

import fr.revoicechat.notification.model.NotificationPayload;

enum PayloadClassesHolder {
  INSTANCE(getPayloadClasses());

  private final Set<Class<? extends NotificationPayload>> payloads;

  PayloadClassesHolder(final Set<Class<? extends NotificationPayload>> payloads) {
    this.payloads = payloads;
  }

  private static Set<Class<? extends NotificationPayload>> getPayloadClasses() {
    Reflections reflections = new Reflections("fr.revoicechat");
    return reflections.getSubTypesOf(NotificationPayload.class);
  }

  public Set<Class<? extends NotificationPayload>> getPayloads() {
    return payloads;
  }
}