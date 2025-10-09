#!/bin/sh

FLAG=/etc/ab_initialized

if [ -f "$FLAG" ]; then
  exit 0
fi

# Find partitions by label
ROOTB=$(blkid -L rootfsB)
ROOTA=$(blkid -L rootfsA)

if [ -z "$ROOTB" ] || [ -z "$ROOTA" ]; then
  echo "rootfsA or rootfsB not found"
  exit 1
fi

# ensure device paths
DEVROOTB=$(readlink -f /dev/disk/by-label/rootfsB)
DEVROOTA=$(readlink -f /dev/disk/by-label/rootfsA)

# mkfs if needed (only if blank)
if ! blkid $DEVROOTB >/dev/null 2>&1; then
  mkfs.ext4 -F $DEVROOTB
fi

# mount, copy (exclude /proc /sys /dev /tmp /boot)
mountpoint=/mnt/newroot
mkdir -p $mountpoint
mount $DEVROOTB $mountpoint

# Use rsync to copy everything except special pseudo-filesystems and /boot (boot is shared)
rsync -aHAX --exclude=/dev --exclude=/proc --exclude=/sys --exclude=/tmp --exclude=/run --exclude=/boot / $mountpoint


# Sync to ensure all data is written
sync

# create fstab or adjust as needed on new root (optional)
# touch flag
touch $FLAG

umount $mountpoint
