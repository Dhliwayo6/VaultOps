-- Backfill user roles to USER for any users that don't have one
UPDATE `users` SET `role` = 'USER' WHERE `role` IS NULL OR `role` = '';
