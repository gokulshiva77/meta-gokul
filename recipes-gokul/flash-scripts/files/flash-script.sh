#!/bin/bash

WIC_GZ_FILE=$(ls *.wic.gz 2>/dev/null | head -n 1)

if [ -z "$WIC_GZ_FILE" ]; then
    echo "❌ No .wic.gz file found in current folder."
    exit 1
fi

echo "✅ Found image: $WIC_GZ_FILE"


if [ -f "$WIC_GZ_FILE" ]; then
    echo "🔍 Decompressing $WIC_GZ_FILE ..."
    gunzip -k "$WIC_GZ_FILE"
    if [ $? -ne 0 ]; then
        echo "❌ Failed to decompress $WIC_GZ_FILE"
        exit 1
    fi
    echo "✅ Decompressed to $WIC_FILE"
else
    echo "ℹ️  $WIC_FILE file does not  exists."
fi

WIC_FILE=$(ls *.wic 2>/dev/null | head -n 1)
# Get removable disks using your command
mapfile -t DISKS < <(lsblk -dpno NAME,SIZE,MODEL,TYPE | grep "disk" | grep "CRW -MS")

if [ ${#DISKS[@]} -eq 0 ]; then
    echo "❌ No removable disks found."
    exit 1
fi

# Show disks with numbers
echo "📀 Available removable disks:"
for i in "${!DISKS[@]}"; do
    echo "[$i] ${DISKS[$i]}"
done

# Ask user to select a disk number
read -p "Enter the number of the disk to flash: " NUM
if ! [[ "$NUM" =~ ^[0-9]+$ ]] || [ "$NUM" -ge "${#DISKS[@]}" ]; then
    echo "❌ Invalid selection."
    exit 1
fi

DEV=$(echo "${DISKS[$NUM]}" | awk '{print $1}')
echo "⚠️  Selected device: $DEV"

echo "⚠️  WARNING: This will erase all data on $DEV"
read -p "Are you sure? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "Aborted."
    exit 1
fi

# Flash the image
echo "🚀 Flashing $WIC_FILE to $DEV ..."
sudo dd if="$WIC_FILE" of="$DEV" bs=4M status=progress conv=fsync

echo "✅ Done! You can now eject $DEV and boot your Pi."